package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.Header;

import butterknife.InjectView;
import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.adapter.DictionariesAdapter;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class DictionariesFragment extends BaseFragment {

    @InjectView(R.id.list_dictionaries)
    ListView mListDictionaries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusUtils.getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dictionaries, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListDictionaries.getAdapter() == null) {
            requestDictionaries();
        }
    }

    private void requestDictionaries() {
        RestClient.get("dictionaries/", new RestClient.GsonHandler<Dictionary[]>(Dictionary[].class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, Dictionary[] response) {
                mListDictionaries.setAdapter(new DictionariesAdapter(getActivity(), response));
            }
        });
    }

    @OnItemClick(R.id.list_dictionaries)
    @SuppressWarnings("unused")
    void onDictionaryClicked(int position) {
        Dictionary dictionary = (Dictionary) mListDictionaries.getItemAtPosition(position);
        EventBusUtils.getBus().post(dictionary);
    }

    void onEvent() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.getBus().unregister(this);
    }
}
