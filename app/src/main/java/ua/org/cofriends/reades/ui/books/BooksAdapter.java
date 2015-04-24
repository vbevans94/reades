package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.view.View;

import com.squareup.picasso.Picasso;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.basic.tools.BindableArrayAdapter;

public class BooksAdapter extends BindableArrayAdapter<Book> {

    final int size;
    private final Picasso picasso;

    public BooksAdapter(Context context, Picasso picasso) {
        super(context, R.layout.item_book);

        size = (int) context.getResources().getDimension(R.dimen.item_icon_size);
        this.picasso = picasso;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public void bindView(Book item, int position, View view) {
        BookItemView itemView = (BookItemView) view;
        itemView.bind(item, picasso, size);
    }
}
