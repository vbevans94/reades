package ua.org.cofriends.reades.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.tools.SemiCircleTarget;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;

public class DictionaryAdapter extends SwipeAdapter<Dictionary> {

    private final int mDetailsRes;
    private final int mSize;

    public DictionaryAdapter(Context context, List<Dictionary> items, int detailsRes) {
        super(context, R.layout.item_dictionary, items);

        mDetailsRes = detailsRes;
        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
    }

    @Override
    public long getItemIdImpl(int position) {
        return getItem(position).getDictionaryId();
    }

    @Override
    public View getViewImpl(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.item_dictionary, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Dictionary dictionary = getItem(position);

        holder.textDetails.setText(mDetailsRes);
        holder.textName.setText(dictionary.getName());
        Picasso.with(getContext())
                .load(dictionary.getFromLanguage().getImageUrl())
                .resize(mSize, mSize)
                .centerCrop()
                .into(holder.targetFrom);
        Picasso.with(getContext())
                .load(dictionary.getToLanguage().getImageUrl())
                .resize(mSize, mSize)
                .centerCrop()
                .into(holder.targetTo);

        return view;
    }

    static class ViewHolder extends BaseViewHolder {

        @InjectView(R.id.text_name)
        TextView textName;

        @InjectView(R.id.text_details)
        TextView textDetails;

        @InjectView(R.id.image_from)
        ImageView imageFrom;

        @InjectView(R.id.image_to)
        ImageView imageTo;

        Target targetTo;

        Target targetFrom;

        ViewHolder(View view) {
            super(view);

            targetFrom = new SemiCircleTarget(imageFrom, SemiCircleTarget.Side.LEFT);
            targetTo = new SemiCircleTarget(imageTo, SemiCircleTarget.Side.RIGHT);
        }
    }
}
