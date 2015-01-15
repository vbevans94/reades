package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.util.AttributeSet;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.BookDownloadService;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.BaseListLayout;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksView extends BaseListLayout implements RestClient.Handler<Book[]> {

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
    public void refreshList() {
        // load books from server
        RestClient.get(String.format("/languages/%d/books/"
                , SavedDictionariesService.getCurrent().getFromLanguage().getLanguageId())
                , RestClient.GsonHandler.create(Book[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Book[] response) {
        mBooks.clear();
        mBooks.addAll(Arrays.asList(response));
        reloadFromDatabase();
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
    public void onEventMainThread(Book.DoneEvent event) {
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
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        listView().setAdapter(new BookAdapter(getContext(), mBooks));

        refreshed();
    }
}
