package ua.org.cofriends.reades.ui.basic.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for single type views in the list.
 *
 * @param <T> type of underlying data items
 */
public class BindableArrayAdapter<T> extends BindableAdapter<T> {

    private int itemLayoutId;
    private List<T> items = new ArrayList<>();

    public BindableArrayAdapter(Context context, int itemLayoutId) {
        super(context);

        this.itemLayoutId = itemLayoutId;
    }

    public void replaceWith(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void remove(T item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        items.add(position, item);
        notifyDataSetChanged();
    }

    public void appendLoaded(List<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * @return underlying items, use on your own risk
     */
    public List<T> getSource() {
        return items;
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Create a new instance of a view for the specified position.
     */
    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(itemLayoutId, container, false);
    }

    /**
     * Bind the data for the specified {@code position} to the view.
     */
    public void bindView(T item, int position, View view) {
    }
}
