package ua.org.cofriends.reades.ui.basic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

public abstract class ListAddActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getSavedViewId());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAdd(AddEvent event) {
        View view = View.inflate(this, getDownloadViewId(), null);
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    /**
     * @return fragment to show under 'download' tab
     */
    abstract protected int getDownloadViewId();

    abstract protected int getSavedViewId();

    public static class AddEvent {}
}
