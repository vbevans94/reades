package ua.org.cofriends.reades.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.data.api.ApiModule;
import ua.org.cofriends.reades.utils.Logger;

@Module(
        includes = ApiModule.class,
        complete = false,
        library = true
)
public final class DataModule {

    private static final String TAG = Logger.makeLogTag(DataModule.class);
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application application) {
        return createOkHttpClient(application);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Logger.e(TAG, "Failed to load image: " + uri.toString(), e);
                    }
                })
                .build();
    }

    private static OkHttpClient createOkHttpClient(Application application) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(application.getCacheDir(), "http");
            client.setCache(new Cache(cacheDir, DISK_CACHE_SIZE));
        } catch (IOException e) {
            Logger.e(TAG, "Unable to install disk cache.", e);
        }

        return client;
    }
}