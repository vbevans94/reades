package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.tools.SwipeToRefreshModule;

public class BaseListLayout extends BaseFrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.text_title)
    protected TextView mTextTitle;

    @InjectView(R.id.layout_refresh)
    SwipeRefreshLayout mLayoutRefresh;

    @Inject
    @SwipeToRefreshModule.ForSwipe
    protected SwipeToRefreshModule.RefreshController refreshController;

    public BaseListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.base_list_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ObjectGraph objectGraph = BaseActivity.get(getContext()).getActivityGraph();
        objectGraph.plus(new SwipeToRefreshModule(mLayoutRefresh, this)).inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        TextView textEmpty = ButterKnife.findById(this, R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        mListView.setEmptyView(textEmpty);

        BaseActivity.get(getContext()).inject(this);

        refreshController.refresh();
    }

    public void refreshed() {
        mLayoutRefresh.setRefreshing(false);
    }

    public ListView listView() {
        return mListView;
    }

    /**
     * Refreshes the list of items from corresponding source.
     */
    @Override
    public void onRefresh() {
    }
}
