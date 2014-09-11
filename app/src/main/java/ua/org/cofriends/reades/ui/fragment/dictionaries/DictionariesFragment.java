package ua.org.cofriends.reades.ui.fragment.dictionaries;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.ui.fragment.BaseFragment;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class DictionariesFragment extends BaseFragment {

    @InjectView(R.id.list)
    ListView mListDictionaries;

    List<Dictionary> mDictionaries;

    private final EventTransmitter mEventTransmitter = new EventTransmitter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textEmpty = ButterKnife.findById(view, R.id.text_empty);
        textEmpty.setText(R.string.message_no_dictionaries);
        mListDictionaries.setEmptyView(textEmpty);

        EventBusUtils.getBus().register(mEventTransmitter);
    }

    /**
     * Refreshes the list of dictionaries from corresponding source.
     */
    void requestDictionaries() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBusUtils.getBus().unregister(mEventTransmitter);
    }

    class EventTransmitter {

        @SuppressWarnings("unused")
        public void onEvent(DictionariesActivity.RefreshEvent event) {
            requestDictionaries();
        }
    }
}
