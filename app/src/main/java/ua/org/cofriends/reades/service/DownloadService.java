package ua.org.cofriends.reades.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadService extends Service {

    private static final int NOTIFICATION_ID = 1994;
    private Set<String> mPendingSet;

    /**
     * Starts load of db if it's not started yet.
     * @param context to use
     * @param loadable to download
     */
    public static void start(Context context, Loadable loadable) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtras(BundleUtils.writeToBundle(loadable));
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPendingSet = new HashSet<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final Loadable loadable = BundleUtils.fetchFromBundle(Loadable.class, intent.getExtras());
        if (!mPendingSet.contains(loadable.getUrl())) {
            // tell user that the download started
            Toast.makeText(getApplicationContext()
                    , getString(R.string.message_download_started, loadable.getName())
                    , Toast.LENGTH_LONG).show();

            // save from being downloaded few times
            mPendingSet.add(loadable.getUrl());

            // create notification about the foreground
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentText(getString(R.string.message_file_loading))
                    .setContentTitle(loadable.getName())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
            startForeground(NOTIFICATION_ID, notification);

            // start loading
            RestClient.getClient().get(RestClient.getAbsoluteUrl(loadable.getUrl()), new FileAsyncHttpResponseHandler(getApplicationContext()) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    stopWithMessage(getString(R.string.error_download_failed));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    stopWithMessage(getString(R.string.message_download_success, loadable.getName()));
                    EventBusUtils.getBus().post(new Loadable.LoadedEvent(loadable));
                }

                /**
                 * Shows corresponding message.
                 * @param message to show user
                 */
                private void stopWithMessage(String message) {
                    // tell user of the result of action
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

        String getUrl();

        String getName();

        public static class LoadedEvent extends EventBusUtils.Event<Loadable> {

            public LoadedEvent(Loadable object) {
                super(object);
            }
        }
    }
}
