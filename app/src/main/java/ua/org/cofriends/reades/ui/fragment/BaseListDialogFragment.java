package ua.org.cofriends.reades.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.ApiError;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.ui.tools.UiUtils;
import ua.org.cofriends.reades.ui.tools.refreshable.RefreshManager;
import ua.org.cofriends.reades.ui.tools.refreshable.Refreshable;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class BaseListDialogFragment extends DialogFragment implements Refreshable, RestClient.ErrorHandler {

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.text_title)
    protected TextView mTextTitle;

    private final RefreshManager mRefreshManager = new RefreshManager(this);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return new AlertDialog.Builder(getActivity())
                .setView(mRefreshManager.onCreateView(inflater, null, savedInstanceState))
                .setTitle(getActivity().getString(R.string.title_store))
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public void initView() {
        ButterKnife.inject(this, getDialog());
        BusUtils.register(this);

        UiUtils.hide(mTextTitle);

        mRefreshManager.onViewCreated(getDialog().getWindow().getDecorView(), null);
    }

    @Override
    public void onResume() {
        super.onResume();

        mRefreshManager.onResume();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, ApiError error) {
        BaseToast.show(getActivity(), error.getDetail());
    }

    /**
     * Refreshes the list of items from corresponding source.
     */
    @Override
    public void refreshList() {}

    @Override
    public ListView listView() {
        if (mListView == null) {
            initView();
        }

        return mListView;
    }

    @Override
    public void refreshed() {
        mRefreshManager.onRefreshed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
        BusUtils.unregister(this);
    }
}
