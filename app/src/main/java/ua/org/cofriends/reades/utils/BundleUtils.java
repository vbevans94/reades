package ua.org.cofriends.reades.utils;

import android.os.Bundle;

import java.util.ArrayList;

public class BundleUtils {

    public static final long DEFAULT_LONG_VALUE = 0l;
    public static final int DEFAULT_INT_VALUE = 0;
    public static final String DEFAULT_STRING_VALUE = null;
    public static final boolean DEFAULT_BOOLEAN_VALUE = false;
    public static final float DEFAULT_FLOAT_VALUE = 0f;

    public static boolean contains(Bundle bundle, String key) {
        return bundle != null && bundle.containsKey(key);
    }

    /**
     * Retrieves string value from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or {@link #DEFAULT_STRING_VALUE} if nothing found
     */
    public static String getString(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            return DEFAULT_STRING_VALUE;
        }
    }

    /**
     * Puts string value to bundle if passed and creates it if {@code null} passed.
     *
     * @param bundle to put into
     * @param key    to to put under
     * @param value  to put
     * @return bundle with the newly put value
     */
    public static Bundle putString(Bundle bundle, String key, String value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, value);
        return bundle;
    }

    /**
     * Retrieves string value from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or {@link #DEFAULT_STRING_VALUE} if nothing found
     */
    public static Boolean getBoolean(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getBoolean(key);
        } else {
            return DEFAULT_BOOLEAN_VALUE;
        }
    }

    /**
     * Puts boolean value to bundle if passed and creates it if {@code null} passed.
     *
     * @param bundle to put into
     * @param key    to to put under
     * @param value  to put
     * @return bundle with the newly put value
     */
    public static Bundle putBoolean(Bundle bundle, String key, Boolean value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(key, value);
        return bundle;
    }

    /**
     * Retrieves int value from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or {@link #DEFAULT_INT_VALUE} if nothing found
     */
    public static int getInt(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getInt(key);
        } else {
            return DEFAULT_INT_VALUE;
        }
    }

    /**
     * Puts int value to bundle bundle if passed and creates it if {@code null} passed.
     *
     * @param bundle to put into
     * @param key    to to put under
     * @param value  to put
     * @return bundle with the newly put value
     */
    public static Bundle putInt(Bundle bundle, String key, int value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, value);
        return bundle;
    }

    /**
     * Retrieves long value from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or {@link #DEFAULT_LONG_VALUE} if nothing found
     */
    public static long getLong(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getLong(key);
        } else {
            return DEFAULT_LONG_VALUE;
        }
    }

    /**
     * Puts long value into a bundle. If bundle is null new one will be created.
     *
     * @param bundle to put into
     * @param key    to put under
     * @param value  to put
     * @return bundle with the value
     */
    public static Bundle putLong(Bundle bundle, String key, long value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putLong(key, value);
        return bundle;
    }

    /**
     * Retrieves float value from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or {@link #DEFAULT_FLOAT_VALUE} if nothing found
     */
    public static float getFloat(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getFloat(key);
        } else {
            return DEFAULT_FLOAT_VALUE;
        }
    }

    public static Bundle putFloat(Bundle bundle, String key, float value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putFloat(key, value);
        return bundle;
    }

    /**
     * Retrieves {@link java.util.ArrayList} of strings from bundle if present.
     *
     * @param bundle to get from
     * @param key    to search by
     * @return value or empty {@link java.util.ArrayList} if nothing found
     */
    public static ArrayList<String> getArrayList(Bundle bundle, String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getStringArrayList(key);
        } else {
            return new ArrayList<String>();
        }
    }

    /**
     * Writes Gson compatible object into the bundle.
     *
     * @param clazz  type of t
     * @param t      item_book we want to persist
     * @param bundle to persist into. If null passed new one will be created
     * @param <T>    type of t
     * @return bundle with appended data
     */
    public static <T> Bundle writeObject(Class<T> clazz, T t, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (t != null) {
            bundle.putString(clazz.getSimpleName(), GsonUtils.toJson(t));
        }

        return bundle;
    }

    /**
     * Creates bundle and writes Gson compatible object into that bundle.
     *
     * @param t   item_book we want to persist
     * @param <T> type of t
     * @return bundle with appended data
     */
    public static <T> Bundle writeObject(Class<T> clazz, T t) {
        return writeObject(clazz, t, null);
    }

    /**
     * Fetches object from the bundle with {@link com.google.gson.Gson}.
     *
     * @param clazz  of object
     * @param bundle to get from
     * @param <T>    type of object
     * @return fetched object or null if not found
     */
    public static <T> T fetchFromBundle(Class<T> clazz, Bundle bundle) {
        if (bundle != null) {
            return GsonUtils.fromJson(bundle.getString(clazz.getSimpleName()), clazz);
        } else {
            return null;
        }
    }

    /**
     * Removes object from the bundle.
     *
     * @param clazz  of object
     * @param bundle to get from
     * @param <T>    type of object
     */
    public static <T> void removeFromBundle(Class<T> clazz, Bundle bundle) {
        if (bundle != null) {
            bundle.remove(clazz.getSimpleName());
        }
    }
}
