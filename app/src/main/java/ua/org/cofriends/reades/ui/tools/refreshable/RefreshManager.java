package ua.org.cofriends.reades.ui.tools.refreshable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.ui.activity.ListAddActivity;
import ua.org.cofriends.reades.utils.BusUtils;

public class RefreshManager implements SwipeRefreshLayout.OnRefreshListener {

    private final Refreshable mRefreshable;

    @InjectView(R.id.layout_refresh)
    SwipeRefreshLayout mLayoutRefresh;

    public RefreshManager(Refreshable refreshable) {
        mRefreshable = refreshable;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textEmpty = (TextView) view.findViewById(R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        mRefreshable.listView().setEmptyView(textEmpty);

        ButterKnife.inject(this, view);

        mLayoutRefresh.setOnRefreshListener(this);
        mLayoutRefresh.setColorSchemeResources(R.color.indigo, R.color.light_indigo);
    }

    public void onResume() {
        if (mRefreshable.listView().getAdapter() == null) {
            mRefreshable.refreshList();
        }
    }

    @Override
    public void onRefresh() {
        mRefreshable.refreshList();
    }

    public void onRefreshed() {
        mLayoutRefresh.setRefreshing(false);
    }
}
