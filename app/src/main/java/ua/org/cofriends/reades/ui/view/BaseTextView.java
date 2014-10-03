package ua.org.cofriends.reades.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ua.org.cofriends.reades.R;

public class BaseTextView extends TextView {

    public BaseTextView(Context context) {
        super(context);
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        considerTypefont(this, attrs);
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        considerTypefont(this, attrs);
    }

    public static void considerTypefont(TextView textView, AttributeSet attrs) {
        if (textView.isInEditMode()) {
            return;
        }

        Context context = textView.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseTextView, 0, 0);

        try {
            String fontName = a.getString(R.styleable.BaseTextView_font);
            if (fontName == null) {
                fontName = context.getString(R.string.font_name);
            }
            Typeface tf = Typeface.createFromAsset(context.getAssets(), fontName);
            textView.setTypeface(tf);
        } finally {
            a.recycle();
        }
    }
}
