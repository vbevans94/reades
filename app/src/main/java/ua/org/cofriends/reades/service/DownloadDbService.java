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
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDbService extends Service {

    private static final int NOTIFICATION_ID = 1994;
    private Set<String> mPendingSet;

    /**
     * Starts load of db if it's not started yet.
     * @param context to use
     * @param dictionary to download db file for
     */
    public static void startLoadDbService(Context context, Dictionary dictionary) {
        Intent intent = new Intent(context, DownloadDbService.class);
        intent.putExtras(BundleUtils.writeToBundle(dictionary));
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPendingSet = new HashSet<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, intent.getExtras());
        if (!mPendingSet.contains(dictionary.getDbUrl())) {
            // tell user that the download started
            Toast.makeText(getApplicationContext()
                    , getString(R.string.message_dictionary_download_started, dictionary.getName())
                    , Toast.LENGTH_LONG).show();

            // save from being downloaded few times
            mPendingSet.add(dictionary.getDbUrl());

            // create notification about the foreground
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentText(getString(R.string.message_db_loading))
                    .setContentTitle(dictionary.getName())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
            startForeground(NOTIFICATION_ID, notification);

            // start loading
            RestClient.getClient().get(dictionary.getDbUrl(), new FileAsyncHttpResponseHandler(getApplicationContext()) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    stopWithMessage(getString(R.string.error_dictionary_download_failed));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    stopWithMessage(getString(R.string.message_dictionary_download_success, dictionary.getName()));
                }

                private void stopWithMessage(String message) {
                    // tell user of the result of action
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    EventBusUtils.getBus().post(new DbLoadedEvent());
                    mPendingSet.remove(dictionary.getDbUrl());
                    stopForeground(true);
                    stopSelf();
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

    public static class DbLoadedEvent {
    }
}
