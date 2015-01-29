package ua.org.cofriends.reades.ui.basic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

import ua.org.cofriends.reades.utils.BusUtils;

public abstract class ListAddActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getSavedViewId());
    }

    @Override
    protected void onStart() {
        super.onStart();

        BusUtils.register(mAddHandler);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BusUtils.unregister(mAddHandler);
    }

    class AddHandler {

        @SuppressWarnings("unused")
        @Subscribe
        public void onAdd(AddEvent event) {
            View view = View.inflate(ListAddActivity.this, getDownloadViewId(), null);
            new AlertDialog.Builder(ListAddActivity.this)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }

    private final AddHandler mAddHandler = new AddHandler();

    /**
     * @return fragment to show under 'download' tab
     */
    abstract protected int getDownloadViewId();

    abstract protected int getSavedViewId();

    public static class AddEvent {}
}
