package ua.org.cofriends.reades.ui.fragment.books;

import android.widget.ArrayAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.activity.BooksActivity;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksFragment extends RefreshListFragment implements RestClient.Handler<Book[]> {

    private List<Book> mBooks;

    @Override
    protected void refreshList() {
        int dictionaryId = BundleUtils.getInt(getArguments(), BooksActivity.EXTRA_DICTIONARY_ID);
        RestClient.get(String.format("/dictionaries/%d/books/", dictionaryId), RestClient.GsonHandler.create(Book[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Book[] response) {
        mBooks = new ArrayList<Book>(Arrays.asList(response));
        SavedBooksService.loadList(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) mListView.getItemAtPosition(position);
        DownloadService.start(getActivity(), book);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(DownloadService.Loadable.LoadedEvent event) {
        DownloadService.Loadable loadable = event.getData();
        if (loadable instanceof Book) {
            SavedBooksService.save(getActivity(), (Book) event.getData());
        }
    }

    /**
     * Called when book was saved in the database.
     * @param event to get book from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.SavedEvent event) {
        mBooks.remove(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Called when local books query returns.
     * @param event to retrieve books from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        mListView.setAdapter(new SimpleAdapter<Book>(getActivity(), R.layout.item_dictionary_download, mBooks));
    }
}
