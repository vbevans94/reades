package ua.org.cofriends.reades.ui.fragment.books;

import android.widget.ArrayAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.LocalDictionariesService;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksFragment extends RefreshListFragment implements RestClient.Handler<Book[]> {

    private List<Book> mBooks;

    @Override
    protected void refreshList() {
        // TODO: get the dictionary ID and request books by it
        RestClient.get("/books/", RestClient.GsonHandler.create(Book[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Book[] response) {
        mBooks = new ArrayList<Book>(Arrays.asList(response));
        LocalDictionariesService.startService(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) mListView.getItemAtPosition(position);
        // TODO: start loading book to the device
    }

    /**
     * Called when dictionary db file loaded to the file system.
     * @param event to retrieve loaded dictionary from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.LoadedEvent event) {
        mBooks.remove(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Called when local dictionaries query returns.
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        // TODO: create adapter for books
        /*mListView.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_download, mBooks));*/
    }
}
