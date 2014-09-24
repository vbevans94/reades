package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class BaseListFragment extends BaseFragment {

    @InjectView(R.id.list)
    protected ListView mListView;

    private final EventTransmitter mEventTransmitter = new EventTransmitter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textEmpty = ButterKnife.findById(view, R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        mListView.setEmptyView(textEmpty);

        EventBusUtils.getBus().register(mEventTransmitter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListView.getAdapter() == null) {
            refreshList();
        }
    }

    /**
     * Refreshes the list of dictionaries from corresponding source.
     */
    protected void refreshList() {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBusUtils.getBus().unregister(mEventTransmitter);
    }

    class EventTransmitter {

        @SuppressWarnings("unused")
        public void onEvent(DictionariesActivity.RefreshEvent event) {
            refreshList();
        }
    }
}
