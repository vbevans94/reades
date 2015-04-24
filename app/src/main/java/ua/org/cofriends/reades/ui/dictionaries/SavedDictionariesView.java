package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.ListView;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.tools.ContextMenuController;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesView extends AddListLayout implements UndoBarController.AdvancedUndoListener, ContextMenuController.MenuTarget {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    @Inject
    Picasso mPicasso;

    @Inject
    ContextMenuController menuController;

    private DictionaryAdapter mAdapter;

    public SavedDictionariesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAdapter = new DictionaryAdapter(getContext(), R.string.title_open, mPicasso);
        listView().setAdapter(mAdapter);
        textTitle.setText(R.string.title_saved);

        menuController.registerForContextMenu(this);
    }

    @Override
    public void onRefresh() {
        SavedDictionariesService.loadList(getContext());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) listView().getItemAtPosition(position);
        BusUtils.post(new Dictionary.SelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionariesListLoaded(Dictionary.ListLoadedEvent event) {
        mAdapter.replaceWith(event.getData());

        refreshController.onStopRefresh();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionaryActionDone(Dictionary.DoneEvent event) {
        refreshController.refresh();
    }

    private void removeDictionary(int position) {
        Dictionary dictionary = mAdapter.getItem(position);
        mUndoBar.message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Dictionary.class, dictionary))
                .show();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        refreshController.refresh();
    }

    @Override
    public void onHide(Parcelable token) {
        Bundle bundle = (Bundle) token;
        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, bundle);
        SavedDictionariesService.actUpon(getContext(), dictionary, SavedDictionariesService.DELETE);
    }

    @Override
    public void onClear(Parcelable[] parcelables) {

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
                removeDictionary(position);
            }
            return true;
        }
        return false;
    }
}
