package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.DictionaryDownloadService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.adapter.DictionaryAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListDialogFragment;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDictionariesFragment extends BaseListDialogFragment implements RestClient.Handler<Dictionary[]> {

    private List<Dictionary> mDictionaries = new ArrayList<Dictionary>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_online);
    }

    @Override
    public void refreshList() {
        // load dictionaries from server
        RestClient.get("/dictionaries/", RestClient.GsonHandler.create(Dictionary[].class, this, this));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Dictionary[] response) {
        mDictionaries.clear();
        mDictionaries.addAll(Arrays.asList(response));
        reloadDictionariesFromDatabase();
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) listView().getItemAtPosition(position);
        // start loading dictionary to the device
        DictionaryDownloadService.start(getActivity(), dictionary);
    }

    /**
     * Called when local dictionaries query returns.
     *
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.ListLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        listView().setAdapter(new DictionaryAdapter(getActivity()
                , mDictionaries, R.string.title_download));

        refreshed();
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.DoneEvent event) {
        reloadDictionariesFromDatabase();
    }

    private void reloadDictionariesFromDatabase() {
        if (isAdded()) {
            SavedDictionariesService.loadList(getActivity());
        }
    }
}
