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
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.SavedDictionariesService;
import ua.org.cofriends.reades.ui.activity.BaseActivity;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.ui.fragment.BaseListFragment;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DownloadDictionariesFragment extends BaseListFragment implements RestClient.Handler<Dictionary[]> {

    private List<Dictionary> mDictionaries = new ArrayList<Dictionary>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextTitle.setText(R.string.title_online);
    }

    @Override
    protected void refreshList() {
        // load dictionaries from server
        RestClient.get("/dictionaries/", RestClient.GsonHandler.create(Dictionary[].class, this, this));
        // display progress
        BusUtils.post(new BaseActivity.ProgressEndEvent(getActivity()));
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Dictionary[] response) {
        mDictionaries.clear();
        mDictionaries.addAll(Arrays.asList(response));
        SavedDictionariesService.loadList(getActivity());
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        SavedDictionariesFragment.DictionaryWrapper wrapper = (SavedDictionariesFragment.DictionaryWrapper) mListView.getItemAtPosition(position);
        // start loading dictionary to the device
        DownloadService.start(getActivity(), wrapper.getWrapped());
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
        // stop displaying progress
        BusUtils.post(new BaseActivity.ProgressEndEvent(getActivity()));
        mDictionaries.removeAll(event.getData());
        mListView.setAdapter(new SimpleAdapter<SavedDictionariesFragment.DictionaryWrapper>(getActivity()
                , R.layout.item
                , SavedDictionariesFragment.DictionaryWrapper.fromList(getActivity()
                    , R.string.title_download
                    , mDictionaries)));
    }
}
