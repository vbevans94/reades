package ua.org.cofriends.reades.ui.fragment.books;

import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksFragment extends RefreshListFragment implements RestClient.Handler<Book[]> {

    private List<Book> mBooks;
    private DictionaryCache mDictionaryCache = new DictionaryCache(this);

    @Override
    protected void refreshList() {
        RestClient.get(String.format("/dictionaries/%d/books/", mDictionaryCache.getDictionary().getDictionaryId()), RestClient.GsonHandler.create(Book[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Book[] response) {
        mBooks = new ArrayList<Book>(Arrays.asList(response));
        SavedBooksService.loadListByDictionary(getActivity(), mDictionaryCache.getDictionary());
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
            Book book = (Book) event.getData();
            book.setDictionary(mDictionaryCache.getDictionary());
            SavedBooksService.save(getActivity(), book);
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

    public static class DictionaryCache {

        private final Fragment mFragment;
        private Dictionary mDictionary;

        public DictionaryCache(Fragment fragment) {
            mFragment = fragment;
        }

        /**
         * @return cached or retrieved from args dictionary for which we want to show books
         */
        public Dictionary getDictionary() {
            if (mDictionary == null) {
                mDictionary = BundleUtils.fetchFromBundle(Dictionary.class, mFragment.getArguments());
            }
            return mDictionary;
        }
    }
}
