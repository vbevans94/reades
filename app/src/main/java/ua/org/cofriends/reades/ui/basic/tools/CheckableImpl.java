package ua.org.cofriends.reades.ui.basic.tools;

import android.view.View;
import android.widget.Checkable;

public class CheckableImpl implements Checkable {

    private final View mView;
    private boolean mChecked = false;

    public static final int[] CHECKED_STATE = new int[] {
            android.R.attr.state_checked
    };

    public CheckableImpl(View view) {
        mView = view;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            mView.refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }
}
