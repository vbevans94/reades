package ua.org.cofriends.reades.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class DictionariesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dictionaries);

        EventBusUtils.getBus().register(this);
    }

    void onEvent(Dictionary dictionary) {
        // TODO: move to books fragment
        Toast.makeText(this, dictionary.getDbUrl(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBusUtils.getBus().unregister(this);
    }
}
