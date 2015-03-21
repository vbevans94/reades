package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;

public class DeviceBooksAdapter extends ArrayAdapter<Book> {

    final int mSize;

    public DeviceBooksAdapter(Context context, List<Book> items) {
        super(context, R.layout.book_item, items);

        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LibraryBooksAdapter.ViewHolder holder;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.book_item, null);
            holder = new LibraryBooksAdapter.ViewHolder(view);
        } else {
            holder = (LibraryBooksAdapter.ViewHolder) view.getTag();
        }

        Book book = getItem(position);

        holder.textName.setText(book.getName());
        holder.textDetails.setText(book.getFormatType().toString());
        holder.image.setImageResource(R.drawable.ic_launcher);

        return view;
    }
}
