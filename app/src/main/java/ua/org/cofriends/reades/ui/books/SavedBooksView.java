package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.cocosw.undobar.UndoBarController;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedBooksView extends AddListLayout implements UndoBarController.AdvancedUndoListener {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    public SavedBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mTextTitle.setText(R.string.title_saved);
    }

    @Override
    public void refreshList() {
        SavedBooksService.loadListByLanguage(getContext(), SavedDictionariesService.getCurrent().getFromLanguage());
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
                , new BookAdapter(getContext(), event.getData()));

        refreshed();
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Book.DoneEvent event) {
        refreshList();
    }

    @SuppressWarnings("unused")
    public void onEvent(SwipeToRemoveTouchListener.RemoveEvent event) {
        Book book = (Book) event.getData();
        mUndoBar.message(R.string.message_will_be_removed)
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
        SavedBooksService.actUpon(getContext(), book, SavedBooksService.DELETE);
    }

    @Override
    public void onClear() {
    }
}
