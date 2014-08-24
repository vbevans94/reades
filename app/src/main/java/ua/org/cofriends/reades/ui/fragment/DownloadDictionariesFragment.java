package ua.org.cofriends.reades.ui.fragment;

import android.widget.ArrayAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadDictionaryService;
import ua.org.cofriends.reades.service.LocalDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionariesAdapter;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDictionariesFragment extends DictionariesFragment implements RestClient.Handler<Dictionary[]> {

    @Override
    public void onResume() {
        super.onResume();

        if (mListDictionaries.getAdapter() == null) {
            requestDictionaries();
        }
    }

    private void requestDictionaries() {
        RestClient.get("/dictionaries/", new RestClient.GsonHandler<Dictionary[]>(Dictionary[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Dictionary[] response) {
        mDictionaries = new ArrayList<Dictionary>(Arrays.asList(response));
        LocalDictionariesService.startService(getActivity());
    }

    @OnItemClick(R.id.list_dictionaries)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListDictionaries.getItemAtPosition(position);
        // start loading dictionary to the device
        DownloadDictionaryService.startService(getActivity(), dictionary);
    }

    /**
     * Called when dictionary db file loaded to the file system.
     * @param event to retrieve loaded dictionary from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(DownloadDictionaryService.DictionaryLoadedEvent event) {
        mDictionaries.remove(event.getData());
        ((ArrayAdapter) mListDictionaries.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Called when local dictionaries query returns.
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(LocalDictionariesService.DictionariesLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        mListDictionaries.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_download, mDictionaries));
    }
}
