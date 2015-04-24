package ua.org.cofriends.reades.ui.basic;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.basic.tools.SwipeToRefreshModule;
import ua.org.cofriends.reades.utils.BusUtils;

public class BaseListLayout extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list)
    ListView listView;

    @InjectView(R.id.text_title)
    protected TextView textTitle;

    @InjectView(R.id.layout_refresh)
    SwipeRefreshLayout layoutRefresh;

    @Inject
    protected SwipeToRefreshModule.RefreshController refreshController;

    @Inject
    @SwipeToRefreshModule.SwipeListener
    AbsListView.OnScrollListener swipeScrollListener;

    public BaseListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.base_list_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ObjectGraph objectGraph = BaseActivity.get(getContext()).getActivityGraph();
        objectGraph.plus(new SwipeToRefreshModule(layoutRefresh, this)).inject(this);

        TextView textEmpty = ButterKnife.findById(this, R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        listView.setEmptyView(textEmpty);

        BusUtils.register(this);

        listView.setOnScrollListener(swipeScrollListener);

        refreshController.refresh();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        BusUtils.unregister(this);
    }

    public ListView listView() {
        return listView;
    }

    /**
     * Refreshes the list of items from corresponding source.
     */
    @Override
    public void onRefresh() {
    }
}
