package ua.org.cofriends.reades.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;

import ua.org.cofriends.reades.utils.BusUtils;

public class BaseActivity extends ActionBarActivity {

    private ProgressViewer mProgressViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusUtils.register(this);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mProgressViewer = new ProgressViewer();
        BusUtils.register(mProgressViewer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusUtils.unregister(this);
        BusUtils.unregister(mProgressViewer);
    }

    public class ProgressViewer {

        @SuppressWarnings("unused")
        public void onEvent(ProgressStartEvent event) {
            if (BaseActivity.this == event.getData()) {
                setSupportProgressBarIndeterminateVisibility(true);
            }
        }

        @SuppressWarnings("unused")
        public void onEvent(ProgressEndEvent event) {
            if (BaseActivity.this == event.getData()) {
                setSupportProgressBarIndeterminateVisibility(false);
            }
        }
    }

    public static class ProgressStartEvent extends BusUtils.Event<Activity> {

        public ProgressStartEvent(Activity object) {
            super(object);
        }
    }

    public static class ProgressEndEvent extends BusUtils.Event<Activity> {

        public ProgressEndEvent(Activity object) {
            super(object);
        }
    }
}
