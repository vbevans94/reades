package ua.org.cofriends.reades.ui.fragment.books;

import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedBooksFragment extends RefreshListFragment {

    private List<Book> mBooks;
    private DownloadBooksFragment.DictionaryCache mDictionaryCache = new DownloadBooksFragment.DictionaryCache(this);

    @Override
    protected void refreshList() {
        SavedBooksService.loadListByDictionary(getActivity(), mDictionaryCache.getDictionary());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) mListView.getItemAtPosition(position);
        EventBusUtils.getBus().post(new Book.SavedEvent(book));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks = event.getData();
        mListView.setAdapter(new SimpleAdapter<Book>(getActivity(), R.layout.item_dictionary_local, mBooks));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.SavedEvent event) {
        mBooks.add(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
}
