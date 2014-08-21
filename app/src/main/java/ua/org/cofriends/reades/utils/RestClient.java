package ua.org.cofriends.reades.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class RestClient {

    private static final String BASE_URL = "http://10.44.40.55:8000/";

    private final static AsyncHttpClient CLIENT = new AsyncHttpClient();

    static {
        // client authorization
        CLIENT.addHeader("Authorization", "Token d43ef3b12974b8e1f2bd4af186efa749e978712d");
        CLIENT.addHeader(AsyncHttpClient.HEADER_CONTENT_TYPE, "application/json");
    }

    public static <T> void get(String url, GsonHandler<T> responseHandler) {
        CLIENT.get(getAbsoluteUrl(url), responseHandler);
    }

    /**
     * @return client to do all possible actions
     */
    public static AsyncHttpClient getClient() {
        return CLIENT;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static abstract class GsonHandler<T> extends TextHttpResponseHandler {

        private final Class<T> mClass;

        public GsonHandler(Class<T> clazz) {
            mClass = clazz;
        }

        @Override
        public final void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            // TODO: think of error responses
        }

        @Override
        public final void onSuccess(int statusCode, Header[] headers, String responseString) {
            onSuccess(statusCode, headers, GsonUtils.fromJson(responseString, mClass));
        }

        public abstract void onSuccess(int statusCode, Header[] headers, T response);
    }
}