package ua.org.cofriends.reades.ui.fragment.books;

import android.os.Bundle;
import android.os.Parcelable;

import com.cocosw.undobar.UndoBarController;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.SavedBooksService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListFragment;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedBooksFragment extends BaseListFragment implements UndoBarController.AdvancedUndoListener {

    private DownloadBooksFragment.DictionaryCache mDictionaryCache = new DownloadBooksFragment.DictionaryCache(this);

    @Override
    protected void refreshList() {
        SavedBooksService.loadListByDictionary(getActivity(), mDictionaryCache.getDictionary());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) mListView.getItemAtPosition(position);
        BusUtils.post(new Book.SelectedEvent(book));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.ListLoadedEvent event) {
        SwipeAdapter.wrapList(mListView
                , new SimpleAdapter<Book>(getActivity(), R.layout.item_saved, event.getData()));
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
