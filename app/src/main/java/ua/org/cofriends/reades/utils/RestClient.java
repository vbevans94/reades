package ua.org.cofriends.reades.utils;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashSet;
import java.util.Set;

import ua.org.cofriends.reades.entity.ApiError;

public class RestClient {

    private static final String BASE_URL = "hi";
    private static final String BASE_API_URL = BASE_URL + "/api";

    private final static AsyncHttpClient CLIENT = new AsyncHttpClient();
    private final static AsyncHttpClient CLEAR_CLIENT = new AsyncHttpClient();

    private final static Set<Handler> sPendingHandlers = new HashSet<Handler>();

    static {
        CLIENT.addHeader("Authorization", "Token hi");
        CLIENT.addHeader(AsyncHttpClient.HEADER_CONTENT_TYPE, "application/json");
    }

    public static AsyncHttpClient getClient() {
        return CLEAR_CLIENT;
    }

    public static <T> void get(String url, GsonHandler<T> responseHandler) {
        if (!sPendingHandlers.contains(responseHandler.mHandler)) {
            sPendingHandlers.add(responseHandler.mHandler);
            CLIENT.get(getAbsoluteApiUrl(url), responseHandler);
        }
    }

    private static void handlerDone(GsonHandler handler) {
        sPendingHandlers.remove(handler.mHandler);
    }

    public static String getAbsoluteApiUrl(String relativeUrl) {
        return BASE_API_URL + relativeUrl;
    }

    public static class GsonHandler<T> extends TextHttpResponseHandler {

        private final Class<T> mClass;
        private final Handler<T> mHandler;
        private final ErrorHandler mErrorHandler;

        private GsonHandler(Class<T> clazz, Handler<T> handler, ErrorHandler errorHandler) {
            mClass = clazz;
            mHandler = handler;
            mErrorHandler = errorHandler;
        }

        public static <T> GsonHandler<T> create(Class<T> clazz, Handler<T> handler, ErrorHandler errorHandler) {
            return new GsonHandler<T>(clazz, handler, errorHandler);
        }

        @Override
        public final void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            ApiError error;
            try {
                error = responseString != null
                        ? GsonUtils.fromJson(responseString, ApiError.class) // error returned by server
                        : new ApiError(throwable.getMessage()); // raised by system
            } catch (JsonSyntaxException e) {
                error = new ApiError(e.getMessage());
            }
            mErrorHandler.onFailure(statusCode, headers, error);

            handlerDone(this);
        }

        @Override
        public final void onSuccess(int statusCode, Header[] headers, String responseString) {
            mHandler.onSuccess(statusCode, headers, GsonUtils.fromJson(responseString, mClass));

            handlerDone(this);
        }
    }

    public interface Handler<T> {

        public void onSuccess(int statusCode, Header[] headers, T response);
    }

    public interface ErrorHandler {

        public void onFailure(int statusCode, Header[] headers, ApiError error);
    }
}