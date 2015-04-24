package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.tools.ContextMenuController;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class DeviceBooksView extends AddListLayout implements UndoBarController.AdvancedUndoListener, View.OnClickListener, ContextMenuController.MenuTarget {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    @Inject
    ContextMenuController mContextMenu;

    @Inject
    Picasso mPicasso;

    private BooksAdapter mAdapter;

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

        mAdapter = new BooksAdapter(getContext(), mPicasso);
        listView().setAdapter(mAdapter);
        textTitle.setText(R.string.title_opened);

        mContextMenu.registerForContextMenu(this);
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
        mAdapter.replaceWith(event.getData());

        refreshController.onStopRefresh();
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onBookActionDone(Book.DoneEvent event) {
        refreshController.refresh();
    }

    private void removeItem(int position) {
        Book book = mAdapter.getItem(position);
        mUndoBar.message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Book.class, book))
                .show();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        refreshController.refresh();
    }

    @Override
    public void onHide(Parcelable token) {
        Bundle bundle = (Bundle) token;
        Book book = BundleUtils.fetchFromBundle(Book.class, bundle);
        SavedBooksService.actUpon(getContext(), book, SavedBooksService.DELETE);
    }

    @Override
    public void onClear(@NonNull Parcelable[] parcelables) {

    }

    @Override
    public ListView getView() {
        return listView();
    }

    @Override
    public int getMenuRes() {
        return R.menu.menu_for_item;
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item, List<Integer> positions) {
        if (item.getItemId() == R.id.action_delete) {
            for (Integer position : positions) {
                removeItem(position);
            }

            return true;
        }
        return false;
    }

    public static class OpenBookEvent {}
}
