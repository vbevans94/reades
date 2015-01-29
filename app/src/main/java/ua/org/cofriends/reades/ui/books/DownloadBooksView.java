package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.util.AttributeSet;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnItemClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.data.api.ApiService;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.BookDownloadService;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.BaseListLayout;
import ua.org.cofriends.reades.utils.HttpUtils;

public class DownloadBooksView extends BaseListLayout implements Callback<List<Book>> {

    @Inject
    ApiService apiService;

    @Inject
    Picasso mPicasso;

    private List<Book> mBooks = new ArrayList<>();

    public DownloadBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mTextTitle.setText(R.string.title_store);
    }

    @Override
    public void onRefresh() {
        // load books from server
        apiService.listBooks(String.valueOf(SavedDictionariesService.getCurrent().getFromLanguage().getLanguageId()), this);
    }

    @Override
    public void success(List<Book> books, Response response) {
        mBooks.clear();
        mBooks.addAll(books);
        reloadFromDatabase();
    }

    @Override
    public void failure(RetrofitError error) {
        // TODO: handle error
        mRefreshController.onStopRefresh();
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        // download book
        Book book = (Book) listView().getItemAtPosition(position);
        BookDownloadService.start(getContext(), book);
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBookActionDone(Book.DoneEvent event) {
        reloadFromDatabase();
    }

    private void reloadFromDatabase() {
        SavedBooksService.loadListByLanguage(getContext(), SavedDictionariesService.getCurrent().getFromLanguage());
    }

    /**
     * Called when local books query returns.
     * @param event to retrieve books from
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksLoaded(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        listView().setAdapter(new BookAdapter(getContext(), mBooks, mPicasso));

        mRefreshController.onStopRefresh();
    }
}
