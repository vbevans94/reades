package ua.org.cofriends.reades.utils;

import android.app.Application;
import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class PicassoUtil {

    private static final String TAG = Logger.makeLogTag(PicassoUtil.class);
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private static Picasso sPicasso;

    public static Picasso getInstance(Context context) {
        synchronized (TAG) {
            if (sPicasso == null) {
                Application app = (Application) context.getApplicationContext();
                sPicasso = new Picasso.Builder(context)
                        .downloader(new OkHttpDownloader(createOkHttpClient(app)))
                        .build();
            }
        }
        return sPicasso;
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            client.setCache(new Cache(cacheDir, DISK_CACHE_SIZE));
        } catch (IOException e) {
            Logger.e(TAG, "Unable to install disk cache.", e);
        }

        return client;
    }
}
