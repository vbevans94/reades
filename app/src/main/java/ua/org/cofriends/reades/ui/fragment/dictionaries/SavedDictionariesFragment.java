package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.os.Bundle;
import android.os.Parcelable;

import com.cocosw.undobar.UndoBarController;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListFragment;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesFragment extends BaseListFragment implements UndoBarController.AdvancedUndoListener {

    @Override
    protected void refreshList() {
        SavedDictionariesService.loadList(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListView.getItemAtPosition(position);
        BusUtils.post(new Dictionary.SelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        SwipeAdapter.wrapList(mListView
                , new SimpleAdapter<Dictionary>(getActivity(), R.layout.item_saved, event.getData()));
    }

    @SuppressWarnings("unused")
    public void onEvent(SwipeToRemoveTouchListener.RemoveEvent event) {
        new UndoBarController.UndoBar(getActivity())
                .style(UndoBarController.UNDOSTYLE)
                .message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Dictionary.class, (Dictionary) event.getData()))
                .show();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        refreshList();
    }

    @Override
    public void onHide(Parcelable token) {
        Bundle bundle = (Bundle) token;
        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, bundle);
        SavedDictionariesService.delete(getActivity(), dictionary);
    }

    @Override
    public void onClear() {
    }
}
