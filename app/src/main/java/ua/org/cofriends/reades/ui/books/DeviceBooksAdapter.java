package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeAdapter;

public class DeviceBooksAdapter extends SwipeAdapter<Book> {

    final int mSize;

    public DeviceBooksAdapter(Context context, List<Book> items) {
        super(context, R.layout.book_item, items);

        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
    }

    @Override
    public long getItemIdImpl(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public View getViewImpl(int position, View view, ViewGroup viewGroup) {
        BookLibraryAdapter.ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.book_item, null);
            holder = new BookLibraryAdapter.ViewHolder(view);
        } else {
            holder = (BookLibraryAdapter.ViewHolder) view.getTag();
        }

        Book book = getItem(position);

        holder.textName.setText(book.getName());
        holder.textDetails.setText(book.getFormatType().toString());
        holder.image.setImageResource(R.drawable.ic_launcher);

        return view;
    }
}
