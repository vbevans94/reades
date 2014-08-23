package ua.org.cofriends.reades.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.tools.BaseViewHolder;

public class DictionariesAdapter extends ArrayAdapter<Dictionary> {

    private final int mResId;

    public DictionariesAdapter(Context context, int resId, List<Dictionary> dictionaries) {
        super(context, resId, dictionaries);

        mResId = resId;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getDictionaryId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), mResId, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mTextName.setText(getItem(position).getName());

        return view;
    }

    static class ViewHolder extends BaseViewHolder {

        @InjectView(R.id.text_name)
        TextView mTextName;

        ViewHolder(View view) {
            super(view);
        }
    }
}
