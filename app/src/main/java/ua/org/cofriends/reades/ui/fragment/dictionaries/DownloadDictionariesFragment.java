package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.widget.ArrayAdapter;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadDictionaryService;
import ua.org.cofriends.reades.service.LocalDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionariesAdapter;
import ua.org.cofriends.reades.ui.fragment.RefreshListFragment;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDictionariesFragment extends RefreshListFragment implements RestClient.Handler<Dictionary[]> {

    private List<Dictionary> mDictionaries;

    @Override
    protected void refreshList() {
        RestClient.get("/dictionaries/", RestClient.GsonHandler.create(Dictionary[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Dictionary[] response) {
        mDictionaries = new ArrayList<Dictionary>(Arrays.asList(response));
        LocalDictionariesService.startService(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListView.getItemAtPosition(position);
        // start loading dictionary to the device
        DownloadDictionaryService.startService(getActivity(), dictionary);
    }

    /**
     * Called when dictionary db file loaded to the file system.
     * @param event to retrieve loaded dictionary from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.LoadedEvent event) {
        mDictionaries.remove(event.getData());
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Called when local dictionaries query returns.
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        mListView.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_download, mDictionaries));
    }
}
