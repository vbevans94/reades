package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.ui.words.WordsDrawerView;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.GoogleApi;

public class BaseActivity extends ActionBarActivity {

    FrameLayout mLayoutContainer;

    private boolean mIntentPending;

    private ObjectGraph mObjectGraph;

    @Inject
    GoogleApi mGoogleApi;

    @Inject
    DrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mObjectGraph = MainApplication.get(this).objectGraph().plus(getModules().toArray());

        super.setContentView(R.layout.base_activity);

        mLayoutContainer = ButterKnife.findById(this, R.id.layout_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mObjectGraph.inject(this);
    }

    public static BaseActivity get(Context context) {
        return (BaseActivity) context;
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    /**
     * Returns list of modules that injects into activity scope.
     * @return array of modules
     */
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new ActivityModule(this));
    }

    public ObjectGraph getActivityGraph() {
        return mObjectGraph;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
    @Subscribe
    public void onHome(WordsDrawerView.HomeEvent event) {
        if (getClass() != DictionariesActivity.class) {
            startActivity(new Intent(this, DictionariesActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        BusUtils.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BusUtils.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onConnectionFailed(GoogleApi.ConnectionFailedEvent event) {
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
