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
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.tools.CircleTransform;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.utils.PicassoUtil;

public class BookAdapter extends SwipeAdapter<Book> {

    final int mSize;

    public BookAdapter(Context context, List<Book> items) {
        super(context, R.layout.item_book, items);

        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
    }

    @Override
    public long getItemIdImpl(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public View getViewImpl(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.item_book, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Book book = getItem(position);

        holder.textName.setText(book.getName());
        holder.textDetails.setText(book.getAuthor().getName());
        PicassoUtil.getInstance(getContext())
                .load(book.getImageUrl())
                .resize(mSize, mSize)
                .transform(new CircleTransform())
                .centerCrop()
                .into(holder.image);

        return view;
    }

    static class ViewHolder extends BaseViewHolder {

        @InjectView(R.id.text_name)
        TextView textName;

        @InjectView(R.id.text_details)
        TextView textDetails;

        @InjectView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            super(view);
        }
    }
}
