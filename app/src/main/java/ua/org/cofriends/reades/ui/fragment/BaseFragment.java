package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import org.apache.http.Header;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.entity.ApiError;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.Logger;
import ua.org.cofriends.reades.utils.RestClient;

public class BaseFragment extends Fragment implements RestClient.ErrorHandler {

    protected final String mTag = Logger.makeLogTag(getClass());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusUtils.getBus().register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.getBus().unregister(this);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, ApiError error) {
        Toast.makeText(getActivity(), error.getDetail(), Toast.LENGTH_LONG).show();
    }
}