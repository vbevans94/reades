package ua.org.cofriends.reades.ui.basic.tools;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.ListView;

import com.cocosw.undobar.UndoBarController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ua.org.cofriends.reades.R;

public class ContextDeleteController implements UndoBarController.AdvancedUndoListener, ContextMenuController.MenuTarget {

    @Inject
    ContextMenuController menuController;

    @Inject
    UndoBarController.UndoBar undoBar;

    private ArrayList<Positioned<Object>> deletedItems;
    private DeleteTarget<Object> target;

    public <T> void registerForDelete(DeleteTarget<T> target) {
        this.target = (DeleteTarget<Object>) target;

        menuController.registerForContextMenu(this);
    }

    @Override
    public ListView getView() {
        return target.getListView();
    }

    @Override
    public int getMenuRes() {
        return R.menu.menu_for_item;
    }

    @Override
    public boolean onMenuItemSelected(MenuItem item, List<Integer> positions) {
        if (item.getItemId() == R.id.action_delete) {
            deletedItems = new ArrayList<>();

            // remember to be able to restore
            for (Integer position : positions) {
                Positioned<Object> positioned = new Positioned<>(position, target.getListView().getItemAtPosition(position));
                deletedItems.add(positioned);
            }

            BindableArrayAdapter adapter = (BindableArrayAdapter) target.getListView().getAdapter();
            for (Positioned<Object> deletedItem : deletedItems) {
                // remove visually
                adapter.remove(deletedItem.getItem());
            }

            undoBar.message(R.string.message_item_deleted)
                    .listener(ContextDeleteController.this)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        // restore items in the list
        if (deletedItems != null) {
            BindableArrayAdapter adapter = (BindableArrayAdapter) target.getListView().getAdapter();
            for (Positioned<Object> positioned : deletedItems) {
                adapter.add(positioned.getPosition(), positioned.getItem());
            }

            deletedItems = null;
        }
    }

    @Override
    public void onHide(Parcelable token) {
        doDelete();
    }

    @Override
    public void onClear(@NonNull Parcelable[] parcelables) {
        doDelete();
    }

    private void doDelete() {
        if (deletedItems != null) {
            for (Positioned<Object> positioned : deletedItems) {
                target.onActualRemove(positioned.getItem());
            }

            deletedItems = null;
        }
    }

    public interface DeleteTarget<T> {

        /**
         * @return list view for which we want to provide delete functionality
         */
        ListView getListView();

        /**
         * Called when item should be removed from data source.
         * @param item to be removed
         */
        void onActualRemove(T item);
    }
}
