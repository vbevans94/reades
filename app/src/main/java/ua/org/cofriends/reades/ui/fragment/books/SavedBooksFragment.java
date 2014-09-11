package ua.org.cofriends.reades.ui.fragment.books;

import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedBooksFragment extends RefreshListFragment {

    private List<Book> mBooks;

    @Override
    protected void refreshList() {
        // TODO: load book list from the source
        /*LocalDictionariesService.startService(getActivity());*/
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) mListView.getItemAtPosition(position);
        EventBusUtils.getBus().post(new Book.SelectedEvent(book));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        mBooks = event.getData();
        // TODO: create adapter for books
        /*mListView.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_local, mBooks));*/
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.LoadedEvent event) {
        mBooks.add(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
}
