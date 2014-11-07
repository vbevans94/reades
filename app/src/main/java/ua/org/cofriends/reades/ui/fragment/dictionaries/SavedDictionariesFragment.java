package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import com.cocosw.undobar.UndoBarController;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionaryAdapter;
import ua.org.cofriends.reades.ui.fragment.AddListFragment;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesFragment extends AddListFragment implements UndoBarController.AdvancedUndoListener {

    @Override
    public void refreshList() {
        SavedDictionariesService.loadList(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_saved);
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) listView().getItemAtPosition(position);
        BusUtils.post(new Dictionary.SelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        SwipeAdapter.wrapList(listView()
                , new DictionaryAdapter(getActivity()
                    , event.getData()
                    , R.string.title_open));
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
        SavedDictionariesService.actUpon(getActivity(), dictionary, SavedDictionariesService.DELETE);
    }

    @Override
    public void onClear() {
    }
}
