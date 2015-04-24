package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.ListView;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.tools.ContextMenuController;
import ua.org.cofriends.reades.ui.basic.tools.Positioned;
import ua.org.cofriends.reades.utils.BusUtils;

public class LibraryBooksView extends AddListLayout implements UndoBarController.AdvancedUndoListener, ContextMenuController.MenuTarget {

    @Inject
    UndoBarController.UndoBar undoBar;

    @Inject
    Picasso picasso;

    @Inject
    ContextMenuController menuController;

    private BooksAdapter adapter;
    private ArrayList<Positioned<Book>> deletedBooks;

    public LibraryBooksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        adapter = new BooksAdapter(getContext(), picasso);
        listView().setAdapter(adapter);
        textTitle.setText(R.string.title_saved);

        menuController.registerForContextMenu(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        undoBar.clear();

        return super.onSaveInstanceState();
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

    @SuppressWarnings("unused")
    @Subscribe
    public void onBooksListLoaded(Book.LibraryListLoadedEvent event) {
        adapter.replaceWith(event.getData());

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

    @Override
    public void onUndo(Parcelable parcelable) {
        // restore items in the list
        if (deletedBooks != null) {
            for (Positioned<Book> book : deletedBooks) {
                adapter.add(book.getPosition(), book.getItem());
            }

            deletedBooks = null;
        }
    }

    @Override
    public void onHide(Parcelable token) {
        doDelete();
    }

    @Override
    public void onClear(Parcelable[] parcelables) {
        doDelete();
    }

    private void doDelete() {
        if (deletedBooks != null) {
            for (Positioned<Book> book : deletedBooks) {
                SavedBooksService.actUpon(getContext(), book.getItem(), SavedBooksService.DELETE);
            }

            deletedBooks = null;
        }
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
            deletedBooks = new ArrayList<>();

            for (Integer position : positions) {
                // remove visually
                Positioned<Book> positioned = new Positioned<Book>(position, adapter.getItem(position));
                deletedBooks.add(positioned);
                adapter.remove(positioned.getItem());
            }

            undoBar.message(R.string.message_will_be_removed)
                    .listener(this)
                    .show();
            return true;
        }
        return false;
    }
}
