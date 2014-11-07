package ua.org.cofriends.reades.ui.fragment.books;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.BookDownloadService;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.activity.BaseActivity;
import ua.org.cofriends.reades.ui.adapter.BookAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListDialogFragment;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksFragment extends BaseListDialogFragment implements RestClient.Handler<Book[]> {

    private List<Book> mBooks = new ArrayList<Book>();
    private DictionaryCache mDictionaryCache = new DictionaryCache(this);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_online);
    }

    @Override
    public void refreshList() {
        // load books from server
        RestClient.get(String.format("/dictionaries/%d/books/"
                , mDictionaryCache.getDictionary().getDictionaryId())
                , RestClient.GsonHandler.create(Book[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Book[] response) {
        mBooks.clear();
        mBooks.addAll(Arrays.asList(response));
        SavedBooksService.loadListByDictionary(getActivity(), mDictionaryCache.getDictionary());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        // download book
        Book book = (Book) listView().getItemAtPosition(position);
        BookDownloadService.start(getActivity(), book);
    }

    /**
     * Called when local books query returns.
     * @param event to retrieve books from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks.removeAll(event.getData());
        listView().setAdapter(new BookAdapter(getActivity(), mBooks));
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
