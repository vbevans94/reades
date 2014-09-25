package ua.org.cofriends.reades.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import ua.org.cofriends.reades.utils.BusUtils;

public class BaseViewPager extends ViewPager {

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        BusUtils.post(new SizeChangedEvent());
    }

    public static class SizeChangedEvent {}
}
