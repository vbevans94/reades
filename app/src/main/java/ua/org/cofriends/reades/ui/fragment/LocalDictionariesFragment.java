package ua.org.cofriends.reades.ui.fragment;

import android.widget.ArrayAdapter;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadDictionaryService;
import ua.org.cofriends.reades.service.LocalDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionariesAdapter;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class LocalDictionariesFragment extends DictionariesFragment {

    @Override
    public void onResume() {
        super.onResume();

        if (mListDictionaries.getAdapter() == null) {
            LocalDictionariesService.startService(getActivity());
        }
    }

    @OnItemClick(R.id.list_dictionaries)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListDictionaries.getItemAtPosition(position);
        EventBusUtils.getBus().post(new DictionarySelectedEvent(dictionary));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(LocalDictionariesService.DictionariesLoadedEvent event) {
        mDictionaries = event.getData();
        mListDictionaries.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_local, mDictionaries));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(DownloadDictionaryService.DictionaryLoadedEvent event) {
        mDictionaries.add(event.getData());
        ((ArrayAdapter) mListDictionaries.getAdapter()).notifyDataSetChanged();
    }

    public static class DictionarySelectedEvent extends EventBusUtils.Event<Dictionary> {

        public DictionarySelectedEvent(Dictionary object) {
            super(object);
        }
    }
}
