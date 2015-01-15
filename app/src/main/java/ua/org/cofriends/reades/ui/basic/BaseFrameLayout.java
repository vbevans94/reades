package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.apache.http.Header;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.entity.ApiError;
import ua.org.cofriends.reades.ui.basic.tools.BaseToast;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class BaseFrameLayout extends FrameLayout implements RestClient.ErrorHandler {

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        BaseActivity.get(getContext()).inject(this);
        BusUtils.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        BusUtils.unregister(this);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, ApiError error) {
        BaseToast.show(getContext(), error.getDetail());
    }
}
