package ua.org.cofriends.reades.ui.basic.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SemiCircleTarget implements Target {

    private final Side mSide;
    private final ImageView mImageView;

    public SemiCircleTarget(ImageView imageView, Side side) {
        mSide = side;
        mImageView = imageView;
    }

    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        float r = size / 2f;

        if (mSide == Side.RIGHT) {
            x += r;
        }

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, (int) r, size);
//        if (squaredBitmap != source) {
//            source.recycle();
//        }

        Bitmap bitmap = Bitmap.createBitmap((int) r, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        Path path = new Path();
        RectF ovalSemiCircle = new RectF();
        float startAngle = mSide == Side.LEFT
                ? getSemicircle(r, size, r, 0, ovalSemiCircle)
                : getSemicircle(0, 0, 0, size, ovalSemiCircle);
        path.addArc(ovalSemiCircle, startAngle, 180);
        canvas.drawPath(path, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    /**
     * Draws semi-circle and returns start angle and rectOut.
     * Semi-circle will be created from start to end points using clock wise direction.
     *
     * @param xStart      vector start point
     * @param yStart
     * @param xEnd        vector end point
     * @param yEnd
     * @param ovalRectOUT RectF to store result
     * @return start angle
     */
    public static float getSemicircle(float xStart, float yStart, float xEnd,
                                      float yEnd, RectF ovalRectOUT) {

        float centerX = xStart + ((xEnd - xStart) / 2);
        float centerY = yStart + ((yEnd - yStart) / 2);

        double xLen = (xEnd - xStart);
        double yLen = (yEnd - yStart);
        float radius = (float) (Math.sqrt(xLen * xLen + yLen * yLen) / 2);

        RectF oval = new RectF((centerX - radius), (centerY - radius), (centerX + radius), (centerY + radius));

        ovalRectOUT.set(oval);

        double radStartAngle = Math.atan2(yStart - centerY, xStart - centerX);

        return (float) Math.toDegrees(radStartAngle);
    }

    /**
     * What side of circle we need
     */
    public enum Side {
        LEFT, RIGHT
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mImageView.setImageBitmap(transform(bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}