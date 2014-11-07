package ua.org.cofriends.reades.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.fragment.WordsDrawerFragment;
import ua.org.cofriends.reades.utils.BusUtils;

public class BaseActivity extends ActionBarActivity {

    FrameLayout mLayoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_drawer);

        mLayoutContainer = ButterKnife.findById(this, R.id.layout_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_drawer, new WordsDrawerFragment())
                    .commit();
        }

        BusUtils.register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets content view under given resource id.
     *
     * @param layoutResId to be set as a content view
     * @see {@link #setContentView(android.view.View)}.
     */
    @Override
    public void setContentView(int layoutResId) {
        setContentView(getLayoutInflater().inflate(layoutResId, null), null);
    }

    /**
     * Sets content view into the content part of alongside with drawer.
     *
     * @param view   to be used as content view
     * @param params to apply when setting up view
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mLayoutContainer == null) {
            throw new IllegalStateException("You can set content view only after super.onCreate() is called!");
        } else {
            mLayoutContainer.removeAllViews();
            if (params == null) {
                mLayoutContainer.addView(view);
            } else {
                mLayoutContainer.addView(view, params);
            }
        }
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusUtils.unregister(this);
    }
}
