package ua.org.cofriends.reades.ui.basic.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class FillTransform implements Transformation {

    private final int mColor;

    public FillTransform(int color) {
        mColor = color;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        canvas.drawBitmap(source, 0, 0, paint);

        return bitmap;
    }

    @Override
    public String key() {
        return "fill";
    }
}