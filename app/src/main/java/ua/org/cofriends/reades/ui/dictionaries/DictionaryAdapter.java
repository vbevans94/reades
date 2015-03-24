package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.basic.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.basic.tools.BindableArrayAdapter;
import ua.org.cofriends.reades.ui.basic.tools.SemiCircleTarget;

public class DictionaryAdapter extends BindableArrayAdapter<Dictionary> {

    private final int mDetailsRes;
    private final int mSize;
    private final Picasso mPicasso;

    public DictionaryAdapter(Context context, int detailsRes, Picasso picasso) {
        super(context, R.layout.item_dictionary);

        mDetailsRes = detailsRes;
        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
        mPicasso = picasso;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getDictionaryId();
    }

    @Override
    public void bindView(Dictionary item, int position, View view) {
        DictionaryItemView itemView = (DictionaryItemView) view;
        itemView.bind(item, mPicasso, mDetailsRes, mSize);
    }
}
