package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import org.apache.http.Header;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.entity.ApiError;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.RestClient;

public class BaseFragment extends Fragment implements RestClient.ErrorHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        MainApplication.get(getActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        BusUtils.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        BusUtils.unregister(this);
        ButterKnife.reset(this);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, ApiError error) {
        BaseToast.show(getActivity(), error.getDetail());
    }
}
