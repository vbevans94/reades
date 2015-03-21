package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.Events;

public class LibraryBooksView extends AddListLayout implements UndoBarController.AdvancedUndoListener {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    @Inject
    Picasso mPicasso;

    public LibraryBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mTextTitle.setText(R.string.title_saved);
    }

    @Override
    public void onRefresh() {
        SavedBooksService.loadList(getContext(), SavedDictionariesService.getCurrent().getFromLanguage(), Book.SourceType.LIBRARY);
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) listView().getItemAtPosition(position);
        BusUtils.post(new Book.SelectedEvent(book));
    }

    @OnItemLongClick(R.id.list)
    @SuppressWarnings("unused")
    boolean onBookLongClicked() {
        // TODO: show dialog with options
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksListLoaded(Book.LibraryListLoadedEvent event) {
        listView().setAdapter(new LibraryBooksAdapter(getContext(), event.getData(), mPicasso));

        mRefreshController.onStopRefresh();
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBookActionDone(Book.DoneEvent event) {
        mRefreshController.refresh();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRemove(Events.RemoveEvent event) {
        Book book = (Book) event.getData();
        mUndoBar.message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Book.class, book))
                .show();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        mRefreshController.refresh();
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
