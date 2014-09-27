package ua.org.cofriends.reades.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.utils.RestClient;

public class SimpleAdapter<T extends SimpleAdapter.Viewable> extends SwipeAdapter<T> {

    public static final int TARGET_SIZE = 128;
    private final int mResId;

    /**
     * Creates adapter for {@link ua.org.cofriends.reades.ui.adapter.SimpleAdapter.Viewable} items.
     * @param context to use
     * @param resId to have {@link android.widget.TextView} or descendant with text_name ID
     * @param items to create adapter for
     */
    public SimpleAdapter(Context context, int resId, List<T> items) {
        super(context, resId, items);

        mResId = resId;
    }

    @Override
    public long getItemIdImpl(int position) {
        return getItem(position).getItemId();
    }

    @Override
    public View getViewImpl(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), mResId, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Viewable item = getItem(position);
        holder.textName.setText(item.getItemName());
        Picasso.with(getContext())
                .load(RestClient.getAbsoluteUrl(item.getImageUrl()))
                .resize(TARGET_SIZE, TARGET_SIZE)
                .centerCrop()
                .into(holder.image);

        return view;
    }

    static class ViewHolder extends BaseViewHolder {

        @InjectView(R.id.text_name)
        TextView textName;

        @InjectView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
        }
    }

    public interface Viewable {

        long getItemId();

        String getItemName();

        String getImageUrl();
    }
}
