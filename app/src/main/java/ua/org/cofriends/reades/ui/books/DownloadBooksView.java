package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.util.AttributeSet;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

public class DownloadBooksView extends BaseListLayout implements Callback<List<Book>> {

    @Inject
    ApiService mApiService;

    @Inject
    Picasso mPicasso;

    private List<Book> mBooks = new ArrayList<>();
    private BooksAdapter mAdapter;

    public DownloadBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAdapter = new BooksAdapter(getContext(), mPicasso);
        listView().setAdapter(mAdapter);

        textTitle.setText(R.string.title_store);
    }

    @Override
    public void onRefresh() {
        // load books from server
        mApiService.listBooks(String.valueOf(SavedDictionariesService.getCurrent().getFromLanguage().getLanguageId()), this);
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
        refreshController.onStopRefresh();
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
     *
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBookActionDone(Book.SavedEvent event) {
        reloadFromDatabase();
    }

    private void reloadFromDatabase() {
        SavedBooksService.loadList(getContext(), SavedDictionariesService.getCurrent().getFromLanguage(), Book.SourceType.LIBRARY);
    }

    /**
     * Called when local books query returns.
     *
     * @param event to retrieve books from
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksLoaded(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        mAdapter.replaceWith(mBooks);

        refreshController.onStopRefresh();
    }
}
