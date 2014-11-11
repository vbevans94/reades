package ua.org.cofriends.reades.ui.tools.refreshable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.ui.activity.ListAddActivity;
import ua.org.cofriends.reades.utils.BusUtils;

public class RefreshManager {

    private final Refreshable mRefreshable;
    private final EventTransmitter mEventTransmitter = new EventTransmitter();

    public RefreshManager(Refreshable refreshable) {
        mRefreshable = refreshable;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textEmpty = (TextView) view.findViewById(R.id.text_empty);
        textEmpty.setText(R.string.message_no_items);
        mRefreshable.listView().setEmptyView(textEmpty);

        BusUtils.register(mEventTransmitter);
    }

    public void onResume() {
        if (mRefreshable.listView().getAdapter() == null) {
            mRefreshable.refreshList();
        }
    }

    public void onDestroyView() {
        BusUtils.unregister(mEventTransmitter);
    }

    class EventTransmitter {

        @SuppressWarnings("unused")
        public void onEvent(ListAddActivity.RefreshEvent event) {
            mRefreshable.refreshList();
        }
    }
}
