package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.ui.tools.refreshable.RefreshManager;
import ua.org.cofriends.reades.ui.tools.refreshable.Refreshable;
import ua.org.cofriends.reades.utils.BusUtils;

public class BaseListFragment extends BaseFragment implements Refreshable {

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.text_title)
    protected TextView mTextTitle;

    private final RefreshManager mRefreshManager = new RefreshManager(this);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRefreshManager.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRefreshManager.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mRefreshManager.onResume();
    }

    /**
     * Refreshes the list of items from corresponding source.
     */
    @Override
    public void refreshList() {}

    @Override
    public ListView listView() {
        return mListView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mRefreshManager.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        mRefreshManager.onStop();
    }
}
