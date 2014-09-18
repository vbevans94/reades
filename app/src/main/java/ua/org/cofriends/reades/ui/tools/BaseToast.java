package ua.org.cofriends.reades.ui.tools;

import android.content.Context;
import android.widget.Toast;

public class BaseToast {

    public static void show(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int resText) {
        show(context, context.getString(resText));
    }
}
