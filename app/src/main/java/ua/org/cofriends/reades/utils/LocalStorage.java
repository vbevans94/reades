package ua.org.cofriends.reades.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Iterator;
import java.util.Map;

public enum LocalStorage {

    INSTANCE;

    private static final String BUNDLE_REF = "bundle_ref";
    private static final String NAME = "local_storage";

    private SharedPreferences mPrefs;

    public void init(Context context) {
        mPrefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * Checks if some data exist with the given key.
     *
     * @param key to search
     * @return true if there is some data with the given key
     */
    public boolean contains(String key) {
        return mPrefs.contains(key);
    }

    /**
     * Removes object from local storage under given key.
     * @param key to the object we want to remove
     * @return true on operation success
     */
    public boolean remove(String key) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (getString(key).equals(BUNDLE_REF)) {
            removeBundle(key, editor);
        } else {
            editor.remove(key);
        }
        return editor.commit();
    }

    private void removeBundle(String prefix, SharedPreferences.Editor editor) {
        Map<String, ?> all = mPrefs.getAll();

        for (String key : all.keySet()) {
            if (key.startsWith(prefix)) {
                editor.remove(key);
            }
        }
    }

    public boolean setString(String key, String value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value == null ? "" : value);
        return editor.commit();
    }

    public String getString(String key) {
        try {
            return mPrefs.getString(key, "");
        } catch (ClassCastException e) {
            return ""; // there can be anything, we don't know what for sure
        }
    }

    public boolean setFloat(String key, float value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float getFloat(String key) {
        try {
            return mPrefs.getFloat(key, 0);
        } catch (ClassCastException e) {
            return 0; // there can be anything, we don't know what for sure
        }
    }

    public boolean setInt(String key, int value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public int getInt(String key) {
        try {
            return mPrefs.getInt(key, 0);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public boolean setLong(String key, long value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public long getLong(String key) {
        try {
            return mPrefs.getLong(key, 0);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public boolean setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getBoolean(String key) {
        try {
            return mPrefs.getBoolean(key, false);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Writes bundle to local storage.
     * Bundle must be shallow which means no recursive bundles.
     *
     * @param prefix key for the bundle
     * @param bundle bundle to save
     * @return true if operation completed successfully
     */
    public boolean setBundle(String prefix, Bundle bundle) {
        SharedPreferences.Editor editor = mPrefs.edit();
        Iterator<String> iterator = bundle.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = bundle.get(key);
            String bundleKey = prefix + key;
            if (value instanceof Integer) {
                editor.putInt(bundleKey, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(bundleKey, (Long) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(bundleKey, (Boolean) value);
            } else if (value instanceof String) {
                editor.putString(bundleKey, (String) value);
            }
        }
        editor.putString(prefix, BUNDLE_REF);

        return editor.commit();
    }

    /**
     * Retrieves bundle from local storage.
     * Bundle will be shallow, with no recursive bundles.
     *
     * @param prefix key for the bundle
     * @return bundle by the given key
     */
    public Bundle getBundle(String prefix) {
        Map<String, ?> all = mPrefs.getAll();
        Bundle bundle = new Bundle();

        assert all != null;
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(prefix)) {
                String bundleKey = key.replace(prefix, "");
                if (value instanceof Integer) {
                    bundle.putInt(bundleKey, (Integer) value);
                } else if (value instanceof Long) {
                    bundle.putLong(bundleKey, (Long) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(bundleKey, (Boolean) value);
                } else if (value instanceof String) {
                    bundle.putString(bundleKey, (String) value);
                }
            }
        }

        return bundle;
    }
}