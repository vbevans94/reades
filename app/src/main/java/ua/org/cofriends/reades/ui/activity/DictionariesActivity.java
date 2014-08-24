package ua.org.cofriends.reades.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.adapter.TabsAdapter;
import ua.org.cofriends.reades.ui.fragment.DownloadDictionariesFragment;
import ua.org.cofriends.reades.ui.fragment.LocalDictionariesFragment;

public class DictionariesActivity extends BaseActivity {

    @InjectView(R.id.pager)
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pager);

        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        TabsAdapter tabsAdapter = new TabsAdapter(this, mPager);

        tabsAdapter.addTab(actionBar.newTab().setText(R.string.title_saved), new LocalDictionariesFragment());
        tabsAdapter.addTab(actionBar.newTab().setText(R.string.title_download), new DownloadDictionariesFragment());
    }

    @SuppressWarnings("unused")
    public void onEvent(LocalDictionariesFragment.DictionarySelectedEvent event) {
        // TODO: move to books activity
        Toast.makeText(this, event.getData().getDbUrl(), Toast.LENGTH_LONG).show();
    }


}
