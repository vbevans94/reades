package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

import com.cocosw.undobar.UndoBarController;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.AddListFragment;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeAdapter;
import ua.org.cofriends.reades.ui.tools.swipetoremove.SwipeToRemoveTouchListener;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedDictionariesFragment extends AddListFragment implements UndoBarController.AdvancedUndoListener {

    @Override
    protected void refreshList() {
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
        DictionaryWrapper wrapper = (DictionaryWrapper) mListView.getItemAtPosition(position);
        BusUtils.post(new Dictionary.SelectedEvent(wrapper.getWrapped()));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        SwipeAdapter.wrapList(mListView
                , new SimpleAdapter<DictionaryWrapper>(getActivity()
                    , R.layout.item
                    , DictionaryWrapper.fromList(getActivity(), R.string.title_open, event.getData())));
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

    public static class DictionaryWrapper implements SimpleAdapter.Viewable {

        private final Dictionary mWrapped;
        private final String mDetails;

        private DictionaryWrapper(Context context, int detailsId, Dictionary dictionary) {
            mWrapped = dictionary;
            mDetails = context.getString(detailsId);
        }

        public Dictionary getWrapped() {
            return mWrapped;
        }

        @Override
        public long getItemId() {
            return mWrapped.getItemId();
        }

        @Override
        public String getItemName() {
            return mWrapped.getItemName();
        }

        @Override
        public String getItemDetails() {
            return mDetails;
        }

        @Override
        public String getImageUrl() {
            return mWrapped.getImageUrl();
        }

        public static List<DictionaryWrapper> fromList(Context context, int detailsId, List<Dictionary> dictionaries) {
            List<DictionaryWrapper> wrappers = new ArrayList<DictionaryWrapper>();
            for (Dictionary dictionary : dictionaries) {
                wrappers.add(new DictionaryWrapper(context, detailsId, dictionary));
            }
            return wrappers;
        }
    }
}
