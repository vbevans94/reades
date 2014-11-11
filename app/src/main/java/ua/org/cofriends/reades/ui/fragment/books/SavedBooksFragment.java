package ua.org.cofriends.reades.ui.fragment.books;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import com.cocosw.undobar.UndoBarController;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.ui.adapter.BookAdapter;
import ua.org.cofriends.reades.ui.fragment.AddListFragment;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedBooksFragment extends AddListFragment implements UndoBarController.AdvancedUndoListener {

    private DownloadBooksFragment.DictionaryCache mDictionaryCache = new DownloadBooksFragment.DictionaryCache(this);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_saved);
    }

    @Override
    public void refreshList() {
        SavedBooksService.loadListByLanguage(getActivity(), mDictionaryCache.getDictionary().getFromLanguage());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) listView().getItemAtPosition(position);
        BusUtils.post(new Book.SelectedEvent(book));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        SwipeAdapter.wrapList(listView()
                , new BookAdapter(getActivity(), event.getData()));
    }

    @SuppressWarnings("unused")
    public void onEvent(SwipeToRemoveTouchListener.RemoveEvent event) {
        Book book = (Book) event.getData();
        new UndoBarController.UndoBar(getActivity())
                .style(UndoBarController.UNDOSTYLE)
                .message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Book.class, book))
                .show();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        refreshList();
    }

    @Override
    public void onHide(Parcelable token) {
        Bundle bundle = (Bundle) token;
        Book book = BundleUtils.fetchFromBundle(Book.class, bundle);
        SavedBooksService.delete(getActivity(), book);
    }

    @Override
    public void onClear() {
    }
}
