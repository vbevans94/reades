package ua.org.cofriends.reades.ui.basic.tools;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuController {

    /**
     * Registers target for context menu.
     *
     * @param target to register
     */
    public void registerForContextMenu(final MenuTarget target) {
        target.getView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        target.getView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private final List<Integer> positions = new ArrayList<>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    if (!positions.contains(position)) {
                        positions.add(position);
                    }
                } else {
                    positions.remove(Integer.valueOf(position));
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(target.getMenuRes(), menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                positions.clear();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                target.onMenuItemSelected(item, positions);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    public interface MenuTarget {

        ListView getView();

        int getMenuRes();

        boolean onMenuItemSelected(MenuItem item, List<Integer> positions);
    }
}
