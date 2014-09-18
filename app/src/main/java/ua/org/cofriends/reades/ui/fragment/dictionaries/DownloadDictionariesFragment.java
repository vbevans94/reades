package ua.org.cofriends.reades.ui.fragment.dictionaries;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
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
        SavedDictionariesService.loadList(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListView.getItemAtPosition(position);
        // start loading dictionary to the device
        DownloadService.start(getActivity(), dictionary);
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(DownloadService.Loadable.LoadedEvent event) {
        DownloadService.Loadable loadable = event.getData();
        if (loadable instanceof Dictionary) {
            SavedDictionariesService.save(getActivity(), (Dictionary) event.getData());
        }
    }

    /**
     * Called when local dictionaries query returns.
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        mListView.setAdapter(new SimpleAdapter<Dictionary>(getActivity(), R.layout.item_dictionary_download, mDictionaries));
    }
}
