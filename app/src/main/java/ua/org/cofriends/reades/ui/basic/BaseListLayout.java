package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;

public class BaseListLayout extends BaseFrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.text_title)
    protected TextView mTextTitle;

    @InjectView(R.id.layout_refresh)
    SwipeRefreshLayout mLayoutRefresh;

    public BaseListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.base_list_layout, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        TextView textEmpty = ButterKnife.findById(this, R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        mListView.setEmptyView(textEmpty);

        mLayoutRefresh.setOnRefreshListener(this);
        mLayoutRefresh.setColorSchemeResources(R.color.indigo, R.color.light_indigo);

        refreshList();
    }

    /**
     * Refreshes the list of items from corresponding source.
     */
    public void refreshList() {}

    public void refreshed() {
        mLayoutRefresh.setRefreshing(false);
    }

    public ListView listView() {
        return mListView;
    }

    @Override
    public void onRefresh() {
        refreshList();
    }
}
