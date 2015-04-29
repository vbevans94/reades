package ua.org.cofriends.reades.utils;

import android.util.Log;

import ua.org.cofriends.reades.BuildConfig;

public class Logger {

    private static final String LOG_PREFIX = "mk_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    static {
        // VolleyLog.DEBUG = true; // change consequently
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void d(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void d(final String tag, String format, Object...params) {
        d(tag, String.format(format, params));
    }

    public static void d(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void v(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void v(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void i(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void i(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    public static void w(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void w(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    public static void e(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void e(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }
}
