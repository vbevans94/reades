package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadDictionaryService;
import ua.org.cofriends.reades.service.LocalDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionariesAdapter;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDictionariesFragment extends BaseFragment implements RestClient.Handler<Dictionary[]> {

    @InjectView(R.id.list_dictionaries)
    ListView mListDictionaries;

    List<Dictionary> mDictionaries;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dictionaries_download, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListDictionaries.getAdapter() == null) {
            requestDictionaries();
        }
    }

    private void requestDictionaries() {
        RestClient.get("dictionaries/", new RestClient.GsonHandler<Dictionary[]>(Dictionary[].class, this, this));
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
    void onEvent(DownloadDictionaryService.DictionaryLoadedEvent event) {
        mDictionaries.remove(event.getData());
        ((ArrayAdapter) mListDictionaries.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Called when local dictionaries query returns.
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    void onEvent(LocalDictionariesService.DictionariesLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        mListDictionaries.setAdapter(new DictionariesAdapter(getActivity(), R.layout.item_dictionary_download, mDictionaries));
    }
}
