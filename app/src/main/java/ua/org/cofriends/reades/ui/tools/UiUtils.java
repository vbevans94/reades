package ua.org.cofriends.reades.ui.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.view.View;
import android.widget.AbsListView;

import java.io.IOException;
import java.io.InputStream;

import ua.org.cofriends.reades.utils.Logger;

public class UiUtils {

    public static final String TRANSLATION_Y = "translationY";
    public static final String TRANSLATION_X = "translationX";
    public static final String Y = "y";
    public static final String X = "x";
    public static final String SCALE_X = "scaleX";
    public static final String SCALE_Y = "scaleY";
    public static final String ROTATION = "rotation";
    public static final String ALPHA = "alpha";
    public static final int BLUR_RADIUS = 10;
    private static final String TAG = Logger.makeLogTag(UiUtils.class);

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void setVisible(boolean visible, View view) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static boolean visible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * Draws bitmap in the center of transparent rectangle of given size.
     *
     * @param context    to use
     * @param drawableId to be drawn
     * @param size       of the resulting drawable
     * @return requested drawable
     */
    public static Bitmap getBitmapOfSize(Context context, int drawableId, Point size) {
        Bitmap destination = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(destination);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), drawableId);
        canvas.drawBitmap(source
                , (destination.getWidth() - source.getWidth()) / 2
                , (destination.getHeight() - source.getHeight()) / 2
                , paint);
        return destination;
    }

    /**
     * Converts drawable to bitmap.
     *
     * @param drawable to convert
     * @return bitmap from that drawable
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Selects list view item_book by actually performing a click on it.
     *
     * @param listView to select in
     * @param position to be selected
     */
    public static void selectListItem(AbsListView listView, int position) {
        View view = listView.getSelectedView();
        if (view == null) {
            view = listView.getChildAt(position + listView.getFirstVisiblePosition());
        }
        listView.performItemClick(view, position, 0l);
    }

    /**
     * Gets bitmap from assets.
     * @param context to use
     * @param filePath to get from
     * @return bitmap
     */
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            stream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            Logger.e(TAG, "Error retrieving bitmap", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // we can't help it
                    Logger.e(TAG, "Error closing stream of bitmap", e);
                }
            }
        }

        return bitmap;
    }

    /**
     * Creates {@link android.graphics.drawable.StateListDrawable} with disabled state as half translucent.
     *
     * @param context to use
     * @param enabled drawable to add alpha to
     * @return drawable that can be disabled
     */
    public static Drawable makeDisablable(Context context, Drawable enabled) {
        StateListDrawable drawable = new StateListDrawable();

        Bitmap enabledBitmap = ((BitmapDrawable) enabled).getBitmap();

        // Setting alpha directly just didn't work, so we draw a new bitmap!
        Bitmap disabledBitmap = Bitmap.createBitmap(
                enabled.getIntrinsicWidth(),
                enabled.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(disabledBitmap);

        Paint paint = new Paint();
        paint.setAlpha(126);
        canvas.drawBitmap(enabledBitmap, 0, 0, paint);

        BitmapDrawable disabled = new BitmapDrawable(context.getResources(), disabledBitmap);

        drawable.addState(new int[]{-android.R.attr.state_enabled}, disabled);
        drawable.addState(StateSet.WILD_CARD, enabled);

        return drawable;
    }
}
