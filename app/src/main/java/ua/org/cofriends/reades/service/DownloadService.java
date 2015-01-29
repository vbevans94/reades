package ua.org.cofriends.reades.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.tools.BaseToast;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.GsonUtils;
import ua.org.cofriends.reades.utils.HttpUtils;
import ua.org.cofriends.reades.utils.Logger;

public class DownloadService extends Service {

    private static final String TAG = Logger.makeLogTag(DownloadService.class);
    private static final int COMMAND_START = 0;
    private static final int COMMAND_STOP = 1;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final String EXTRA_CLASS_NAME = "extra_class_name";
    private static final String EXTRA_JSON = "extra_json";
    private static final String EXTRA_COMMAND = "extra_command";

    private Map<Loadable, Integer> mPendingToId;
    private int mNotificationColor;

    /**
     * Starts load of db if it's not started yet.
     * @param context to use
     * @param loadable to download
     */
    public static void start(Context context, Loadable loadable, Class serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        putLoadable(intent, loadable);
        intent.putExtra(EXTRA_COMMAND, COMMAND_START);
        context.startService(intent);
    }

    /**
     * Creates intent that forces this service to stop exact load.
     * @param context to use
     */
    private static PendingIntent stopIntent(Context context, Loadable loadable, Class serviceClass) {
        Intent intent = new Intent(context, DownloadService.class);
        putLoadable(intent, loadable);
        intent.putExtra(EXTRA_COMMAND, COMMAND_STOP);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    /**
     * Puts loadable inside of the intent.
     * @param intent to put into
     * @param loadable to put
     */
    private static void putLoadable(Intent intent, Loadable loadable) {
        intent.putExtra(EXTRA_CLASS_NAME, loadable.getClass().getName());
        intent.putExtra(EXTRA_JSON, GsonUtils.toJson(loadable));
    }

    /**
     * Gets loadable from the intent.
     * @param intent to get from
     * @return loadable instance or null if failed
     */
    private static Loadable getLoadable(Intent intent) {
        String className = intent.getStringExtra(EXTRA_CLASS_NAME);
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "Error when building class from params", e);
        }
        if (clazz == null) {
            return null;
        }

        return (Loadable) GsonUtils.fromJson(intent.getStringExtra(EXTRA_JSON), clazz);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPendingToId = new HashMap<>();
        mNotificationColor = getResources().getColor(R.color.indigo);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra(EXTRA_COMMAND, 0);

        switch (command) {
            case COMMAND_START:
                startCommand(intent, startId);
                break;

            case COMMAND_STOP:
                stopCommand(intent);
                break;
        }


        return START_NOT_STICKY;
    }

    /**
     * Stops loading.
     * @param intent to fetch needed data from
     */
    private void stopCommand(Intent intent) {
        Loadable loadable = getLoadable(intent);
        if (mPendingToId.containsKey(loadable)) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.cancel(mPendingToId.get(loadable));
            // remove for not being notified on load end
            mPendingToId.remove(loadable);
        }
    }

    /**
     * Starts load.
     * @param intent to fetch needed data from
     * @param startId to start with
     */
    private void startCommand(Intent intent, final int startId) {
        final Loadable loadable = getLoadable(intent);

        if (!mPendingToId.containsKey(loadable)) {
            // tell user that the download started
            BaseToast.show(getApplicationContext()
                    , getString(R.string.message_download_started, loadable.getName()));

            // save from being downloaded few times
            mPendingToId.put(loadable, COUNTER.incrementAndGet());

            // create notification about the foreground
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentText(getString(R.string.message_file_loading, 0))
                    .setContentTitle(loadable.getName())
                    .setContentIntent(stopIntent(this, loadable, getClass()))
                    .setSmallIcon(R.drawable.ic_stat_file_file_download)
                    .setColor(mNotificationColor);
            final NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            startForeground(mPendingToId.get(loadable), builder.build());

            // start loading
            HttpUtils.getClient().get(loadable.getDownloadUrl(), new FileAsyncHttpResponseHandler(this) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    BusUtils.post(new Loadable.FailedEvent());
                    stopWithMessage(getString(R.string.error_download_failed), loadable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    loadable.setLoadedPath(file.getAbsolutePath());

                    onLoaded(loadable);

                    // notify eco-system that we are done
                    BusUtils.post(new Loadable.LoadedEvent(loadable));

                    stopWithMessage(getString(R.string.message_download_success, loadable.getName()), loadable);
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    super.onProgress(bytesWritten, totalSize);

                    if (totalSize > 0) {
                        int progress = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                        builder.setProgress(100, progress, false);
                        manager.notify(mPendingToId.get(loadable)
                                , builder
                                .setContentText(getString(R.string.message_file_loading, progress))
                                .build());
                    }
                }

                /**
                 * Shows corresponding message.
                 * @param message to show user
                 */
                private void stopWithMessage(String message, Loadable loadable) {
                    // only when load isn't cancelled, we cancel it with notifying user
                    if (mPendingToId.containsKey(loadable)) {
                        // tell user of the result of action
                        BaseToast.show(getApplicationContext(), message);
                        mPendingToId.remove(loadable);
                        if (mPendingToId.isEmpty()) {
                            stopForeground(true);
                            stopSelf(startId);
                        }
                    }
                }
            });
        }
    }

    public void onLoaded(Loadable loadable) {
        // designed for overriding
    }

    @Override
    public IBinder onBind(Intent intent) {
        // we don't allow binding
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPendingToId = null;
    }

    public interface Loadable {

        String getDownloadUrl();

        void setLoadedPath(String url);

        String getName();

        public static class LoadedEvent extends BusUtils.Event<Loadable> {

            public LoadedEvent(Loadable object) {
                super(object);
            }
        }

        public static class FailedEvent {}
    }
}
