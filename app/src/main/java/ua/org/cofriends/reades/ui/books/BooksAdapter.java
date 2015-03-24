package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.basic.tools.BaseViewHolder;
import ua.org.cofriends.reades.ui.basic.tools.BindableAdapter;
import ua.org.cofriends.reades.ui.basic.tools.BindableArrayAdapter;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;

public class BooksAdapter extends BindableArrayAdapter<Book> {

    final int mSize;
    private final Picasso mPicasso;

    public BooksAdapter(Context context, Picasso picasso) {
        super(context, R.layout.item_book);

        mSize = (int) context.getResources().getDimension(R.dimen.item_icon_size);
        mPicasso = picasso;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getBookId();
    }

    @Override
    public void bindView(Book item, int position, View view) {
        BookItemView itemView = (BookItemView) view;
        itemView.bind(item, mPicasso, mSize);
    }
}
