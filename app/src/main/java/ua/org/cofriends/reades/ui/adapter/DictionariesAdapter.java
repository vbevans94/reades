package ua.org.cofriends.reades.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.tools.BaseViewHolder;

public class DictionariesAdapter extends ArrayAdapter<Dictionary> {

    public DictionariesAdapter(Context context, Dictionary[] objects) {
        super(context, R.layout.item_dictionary, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.item_dictionary, null);
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
