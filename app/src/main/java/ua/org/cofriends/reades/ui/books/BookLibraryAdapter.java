package ua.org.cofriends.reades.ui.books;

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
import ua.org.cofriends.reades.ui.basic.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeAdapter;

public class BookLibraryAdapter extends SwipeAdapter<Book> {

    final int mSize;
    private final Picasso mPicasso;

    public BookLibraryAdapter(Context context, List<Book> items, Picasso picasso) {
        super(context, R.layout.book_item, items);

        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
        mPicasso = picasso;
    }

    @Override
    public long getItemIdImpl(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public View getViewImpl(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.book_item, null);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Book book = getItem(position);

        holder.textName.setText(book.getName());
        holder.textDetails.setText(book.getAuthor().getName());
        mPicasso.load(book.getImageUrl())
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
