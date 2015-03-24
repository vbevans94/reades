package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;

public class BookItemView extends RelativeLayout {

    @InjectView(R.id.text_name)
    TextView textName;

    @InjectView(R.id.text_details)
    TextView textDetails;

    @InjectView(R.id.image)
    ImageView image;

    public BookItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) {
            return;
        }

        ButterKnife.inject(this);
    }

    public void bind(Book book, Picasso picasso, int size) {
        textName.setText(book.getName());
        if (book.getSourceType() == Book.SourceType.LIBRARY) {
            textDetails.setText(book.getAuthor().getName());
            picasso.load(book.getImageUrl())
                    .resize(size, size)
                    .transform(new CircleTransform())
                    .centerCrop()
                    .into(image);
        } else {
            textDetails.setText(book.getFormatType().toString());
            image.setImageResource(R.drawable.ic_launcher);
        }
    }
}
