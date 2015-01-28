package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.cocosw.undobar.UndoBarController;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.AddListLayout;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.basic.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesView extends AddListLayout implements UndoBarController.AdvancedUndoListener {

    @Inject
    UndoBarController.UndoBar mUndoBar;

    public SavedDictionariesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onRefresh() {
        SavedDictionariesService.loadList(getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mTextTitle.setText(R.string.title_saved);
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
        SwipeAdapter.wrapList(listView()
                , new DictionaryAdapter(getContext()
                    , event.getData()
                    , R.string.title_open));

        refreshed();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDicionaryActionDone(Dictionary.DoneEvent event) {
        refreshController.refresh();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSwipeRemove(SwipeToRemoveTouchListener.RemoveEvent event) {
        mUndoBar.message(R.string.message_will_be_removed)
                .listener(this)
                .token(BundleUtils.writeObject(Dictionary.class, (Dictionary) event.getData()))
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
    public void onClear() {
    }
}
