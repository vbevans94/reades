package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class BaseButton extends Button {

    public BaseButton(Context context) {
        super(context);
    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        BaseTextView.considerTypefont(this, attrs);
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        BaseTextView.considerTypefont(this, attrs);
    }
}
