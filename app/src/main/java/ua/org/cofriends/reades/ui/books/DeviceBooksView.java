package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.Events;

public class DeviceBooksView extends AddListLayout implements UndoBarController.AdvancedUndoListener, View.OnClickListener {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    public DeviceBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViewById(R.id.image_add).setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mTextTitle.setText(R.string.title_opened);
    }

    @Override
    public void onClick(@NonNull View v) {
        BusUtils.post(new OpenBookEvent());
    }

    @Override
    public void onRefresh() {
        SavedBooksService.loadList(getContext(), SavedDictionariesService.getCurrent().getFromLanguage(), Book.SourceType.DEVICE);
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onBookClicked(int position) {
        Book book = (Book) listView().getItemAtPosition(position);
        BusUtils.post(new Book.SelectedEvent(book));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksListLoaded(Book.DeviceListLoadedEvent event) {
        listView().setAdapter(new DeviceBooksAdapter(getContext(), event.getData()));

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

    public static class OpenBookEvent {}
}
