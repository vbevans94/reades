package ua.org.cofriends.reades.ui.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.BusUtils;

public abstract class ListAddActivity extends BaseActivity {

    public static final String DOWNLOAD_FRAGMENT_TAG = "download_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_container, getSavedFragment())
                    .commit();
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(AddEvent event) {
        getDownloadFragment().show(getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null), DOWNLOAD_FRAGMENT_TAG);
    }

    /**
     * @return fragment to show under 'download' tab
     */
    abstract DialogFragment getDownloadFragment();

    /**
     * @return fragment to show under 'saved' tab
     */
    abstract Fragment getSavedFragment();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            BusUtils.post(new RefreshEvent());
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: test whether the it fires in stopped activities/fragments
    public static class RefreshEvent {
    }

    public static class AddEvent {
    }
}
