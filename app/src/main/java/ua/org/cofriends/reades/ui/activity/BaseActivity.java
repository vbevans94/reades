package ua.org.cofriends.reades.ui.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.plus.Plus;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.fragment.WordsDrawerFragment;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.GoogleApi;

public class BaseActivity extends ActionBarActivity {

    FrameLayout mLayoutContainer;

    @Inject
    GoogleApi mGoogleApi;

    private boolean mIntentPending;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainApplication.get(this).inject(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleApi.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mGoogleApi.connect();
            } else {
                mGoogleApi.cancel();
            }
            mIntentPending = false;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(WordsDrawerFragment.HomeEvent event) {
        if (getClass() != DictionariesActivity.class) {
            startActivity(new Intent(this, DictionariesActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        BusUtils.register(this);
        mGoogleApi.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        BusUtils.unregister(this);
        mGoogleApi.disconnect();
    }

    @SuppressWarnings("unused")
    public void onEvent(GoogleApi.ConnectionFailedEvent event) {
        try {
            if (!mIntentPending) {
                mIntentPending = true;
                startIntentSenderForResult(event.getData().getResolution().getIntentSender(),
                        GoogleApi.RC_SIGN_IN, null, 0, 0, 0);
            }
        } catch (IntentSender.SendIntentException e) {
            mGoogleApi.connect();
            mIntentPending = false;
        }
    }
}
