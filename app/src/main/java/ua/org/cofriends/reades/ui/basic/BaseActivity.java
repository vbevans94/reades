package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;

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

public class BaseActivity extends AppCompatActivity {

    FrameLayout layoutContainer;

    private ObjectGraph objectGraph;

    @Inject
    DrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        objectGraph = MainApplication.get(this).objectGraph().plus(getModules().toArray());

        super.setContentView(R.layout.base_activity);

        layoutContainer = ButterKnife.findById(this, R.id.layout_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectGraph.inject(this);
    }

    public static BaseActivity get(Context context) {
        return (BaseActivity) context;
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    /**
     * Returns list of modules that injects into activity scope.
     * @return array of modules
     */
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new ActivityModule(this));
    }

    public ObjectGraph getActivityGraph() {
        return objectGraph;
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
        if (layoutContainer == null) {
            throw new IllegalStateException("You can set content view only after super.onCreate() is called!");
        } else {
            layoutContainer.removeAllViews();
            if (params == null) {
                layoutContainer.addView(view);
            } else {
                layoutContainer.addView(view, params);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, null);
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
}
