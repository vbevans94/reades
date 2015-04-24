package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.util.AttributeSet;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnItemClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.data.api.ApiService;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.DictionaryDownloadService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.BaseListLayout;

public class DownloadDictionariesView extends BaseListLayout implements Callback<List<Dictionary>> {

    @Inject
    ApiService mApiService;

    @Inject
    Picasso mPicasso;

    private final List<Dictionary> mDictionaries = new ArrayList<Dictionary>();
    private DictionaryAdapter mAdapter;

    public DownloadDictionariesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAdapter = new DictionaryAdapter(getContext(), R.string.title_download, mPicasso);
        listView().setAdapter(mAdapter);
        textTitle.setText(R.string.title_store);
    }

    @Override
    public void onRefresh() {
        // load dictionaries from server
        mApiService.listDictionaries(this);
    }

    @Override
    public void success(List<Dictionary> dictionaries, Response response) {
        mDictionaries.clear();
        mDictionaries.addAll(dictionaries);
        reloadDictionariesFromDatabase();
    }

    @Override
    public void failure(RetrofitError error) {
        // TODO: handle error
        refreshController.onStopRefresh();
    }

    @OnItemClick(R.id.list)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) listView().getItemAtPosition(position);
        // start loading dictionary to the device
        DictionaryDownloadService.start(getContext(), dictionary);
    }

    /**
     * Called when local dictionaries query returns.
     *
     * @param event to retrieve dictionaries from
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionariesListLoaded(Dictionary.ListLoadedEvent event) {
        mDictionaries.removeAll(event.getData());
        mAdapter.replaceWith(mDictionaries);

        refreshController.onStopRefresh();
    }

    /**
     * Saved or deleted. Need to refresh list.
     * @param event to respond on
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onDictionaryActionDone(Dictionary.DoneEvent event) {
        reloadDictionariesFromDatabase();
    }

    private void reloadDictionariesFromDatabase() {
        SavedDictionariesService.loadList(getContext());
    }
}
