package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedDictionariesFragment extends RefreshListFragment {

    private List<Dictionary> mDictionaries;

    @Override
    protected void refreshList() {
        SavedDictionariesService.loadList(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListView.getItemAtPosition(position);
        EventBusUtils.getBus().post(new Dictionary.SelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        mDictionaries = event.getData();
        mListView.setAdapter(new SimpleAdapter<Dictionary>(getActivity(), R.layout.item_dictionary_local, mDictionaries));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.SavedEvent event) {
        mDictionaries.add(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
}
