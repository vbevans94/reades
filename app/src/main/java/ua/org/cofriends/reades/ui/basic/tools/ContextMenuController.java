package ua.org.cofriends.reades.ui.basic.tools;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.HashMap;
import java.util.Map;

public class ContextMenuController {

    private final Activity activity;
    private Map<View, MenuTarget> mTargets = new HashMap<>();

    public ContextMenuController(Activity activity) {
        this.activity = activity;
    }

    /**
     * Registers target for context menu.
     * @param target to register
     */
    public void registerForContextMenu(MenuTarget target) {
        mTargets.put(target.getView(), target);
        activity.registerForContextMenu(target.getView());
    }

    public void onCreateMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (mTargets.containsKey(view)) {
            activity.getMenuInflater().inflate(mTargets.get(view).getMenuRes(), menu);
        }
    }

    public void onMenuItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MenuTarget target = mTargets.get(adapterInfo.targetView);
        if (target != null) {
            target.onMenuItemSelected(item, adapterInfo);
        }
    }

    /**
     * Unregisters from context menu for releasing the reference to the view.
     * @param view {@link MenuTarget#getView()} passed in the register call
     */
    public void unregisterView(View view) {
        mTargets.remove(view);
    }

    public interface MenuTarget {

        View getView();

        int getMenuRes();

        boolean onMenuItemSelected(MenuItem item, AdapterView.AdapterContextMenuInfo adapterInfo);
    }
}
