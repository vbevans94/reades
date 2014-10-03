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
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.activity.BaseActivity;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListFragment;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadBooksFragment extends BaseListFragment implements RestClient.Handler<Book[]> {

    private List<Book> mBooks = new ArrayList<Book>();
    private DictionaryCache mDictionaryCache = new DictionaryCache(this);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_online);
    }

    @Override
    protected void refreshList() {
        // load books from server
        RestClient.get(String.format("/dictionaries/%d/books/"
                , mDictionaryCache.getDictionary().getDictionaryId())
                , RestClient.GsonHandler.create(Book[].class, this, this));
        // display progress
        BusUtils.post(new BaseActivity.ProgressEndEvent(getActivity()));
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
        Book book = (Book) mListView.getItemAtPosition(position);
        DownloadService.start(getActivity(), book);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(DownloadService.Loadable.LoadedEvent event) {
        // save downloaded to database
        DownloadService.Loadable loadable = event.getData();
        if (loadable instanceof Book) {
            Book book = (Book) event.getData();
            SavedBooksService.saveWithDictionary(getActivity(), book, mDictionaryCache.getDictionary());
        }
    }

    /**
     * Called when local books query returns.
     * @param event to retrieve books from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        // stop displaying progress
        BusUtils.post(new BaseActivity.ProgressEndEvent(getActivity()));
        mBooks.removeAll(event.getData());
        mListView.setAdapter(new SimpleAdapter<Book>(getActivity(), R.layout.item, mBooks));
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
