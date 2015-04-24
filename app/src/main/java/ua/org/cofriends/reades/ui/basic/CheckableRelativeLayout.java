package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import ua.org.cofriends.reades.ui.basic.tools.CheckableImpl;

/**
 * Author vbevans94.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private final Checkable checkable = new CheckableImpl(this);

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        checkable.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return checkable != null && checkable.isChecked();
    }

    @Override
    public void toggle() {
        checkable.toggle();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            state = mergeDrawableStates(state, CheckableImpl.CHECKED_STATE);
        }
        return state;
    }
}
