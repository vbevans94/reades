package ua.org.cofriends.reades.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.GsonUtils;
import ua.org.cofriends.reades.utils.Logger;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadService extends Service {

    private static final int NOTIFICATION_ID = 1994;
    public static final String EXTRA_CLASS_NAME = "extra_class_name";
    public static final String EXTRA_JSON = "extra_json";
    private static final String TAG = Logger.makeLogTag(DownloadService.class);
    private Set<String> mPendingSet;

    /**
     * Starts load of db if it's not started yet.
     * @param context to use
     * @param loadable to download
     */
    public static void start(Context context, Loadable loadable) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(EXTRA_CLASS_NAME, loadable.getClass().getName());
        intent.putExtra(EXTRA_JSON, GsonUtils.toJson(loadable));
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPendingSet = new HashSet<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String className = intent.getStringExtra(EXTRA_CLASS_NAME);
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "Error when building class from params", e);
        }
        if (clazz == null) {
            return START_NOT_STICKY;
        }

        final Loadable loadable = (Loadable) GsonUtils.fromJson(intent.getStringExtra(EXTRA_JSON), clazz);

        if (!mPendingSet.contains(loadable.getDownloadUrl())) {
            // tell user that the download started
            BaseToast.show(getApplicationContext()
                    , getString(R.string.message_download_started, loadable.getName()));

            // save from being downloaded few times
            mPendingSet.add(loadable.getDownloadUrl());

            // create notification about the foreground
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentText(getString(R.string.message_file_loading))
                    .setContentTitle(loadable.getName())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
            startForeground(NOTIFICATION_ID, notification);

            // start loading
            RestClient.getClient().get(RestClient.getAbsoluteUrl(loadable.getDownloadUrl()), new FileAsyncHttpResponseHandler(getApplicationContext()) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    EventBusUtils.getBus().post(new Loadable.FailedEvent());
                    stopWithMessage(getString(R.string.error_download_failed));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    loadable.setLoadedPath(file.getAbsolutePath());
                    EventBusUtils.getBus().post(new Loadable.LoadedEvent(loadable));
                    stopWithMessage(getString(R.string.message_download_success, loadable.getName()));
                }

                /**
                 * Shows corresponding message.
                 * @param message to show user
                 */
                private void stopWithMessage(String message) {
                    // tell user of the result of action
                    BaseToast.show(getApplicationContext(), message);

                    stopForeground(true);
                    stopSelf(startId);
                }
            });
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // we don't allow binding
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPendingSet = null;
    }

    public interface Loadable {

        String getDownloadUrl();

        void setLoadedPath(String url);

        String getName();

        public static class LoadedEvent extends EventBusUtils.Event<Loadable> {

            public LoadedEvent(Loadable object) {
                super(object);
            }
        }

        public static class FailedEvent {}
    }
}
