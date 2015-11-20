package ua.org.cofriends.reades.utils;

import android.os.Build;

/**
 * Author vbevans94.
 */
public final class Versions {

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
