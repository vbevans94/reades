package ua.org.cofriends.reades.ui.basic.tools.swipetoremove;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class SwipeAdapter<T> extends ArrayAdapter<T> {

    private SwipeToRemoveTouchListener mTouchListener;

    public SwipeAdapter(Context context, int resource, List<T> items) {
        super(context, resource, items);
    }

    public static <T, A extends SwipeAdapter<T>> SwipeAdapter<T> wrapList(ListView listView, A adapter) {
        BackgroundContainer backgroundContainer;
        if (listView.getParent() instanceof BackgroundContainer) {
            // we already wrapped the container up, just get the container
            backgroundContainer = (BackgroundContainer) listView.getParent();
        } else {
            // create fake container
            backgroundContainer = new BackgroundContainer(listView.getContext());
        }
        listView.setAdapter(adapter.setTouchListener(new SwipeToRemoveTouchListener(listView, backgroundContainer)));

        return adapter;
    }

    final SwipeAdapter<T> setTouchListener(SwipeToRemoveTouchListener touchListener) {
        mTouchListener = touchListener;

        return this;
    }

    public abstract View getViewImpl(int position, View view, ViewGroup viewGroup);

    /**
     * Returns unique ID for a given item_book.
     * @param position of item_book we need the ID
     * @return id
     */
    public abstract long getItemIdImpl(int position);

    @Override
    public final boolean hasStableIds() {
        return true;
    }

    @Override
    public final long getItemId(int position) {
        return position < getCount() ? getItemIdImpl(position) : AdapterView.INVALID_POSITION;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = getViewImpl(position, convertView, parent);
        if (mTouchListener != null) {
            view.setOnTouchListener(mTouchListener);
        }

        return view;
    }
}
