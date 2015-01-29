package ua.org.cofriends.reades.utils;

import com.loopj.android.http.AsyncHttpClient;

import ua.org.cofriends.reades.BuildConfig;

public class HttpUtils {

    private final static AsyncHttpClient CLEAR_CLIENT = new AsyncHttpClient();

    public static AsyncHttpClient getClient() {
        return CLEAR_CLIENT;
    }
}