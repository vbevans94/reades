package ua.org.cofriends.reades.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.ui.activity.ListAddActivity;
import ua.org.cofriends.reades.ui.tools.UiUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class AddListFragment extends BaseListFragment {

    @Optional
    @InjectView(R.id.image_add)
    protected View mImageAdd;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiUtils.show(mImageAdd);
    }

    @Optional
    @OnClick(R.id.image_add)
    @SuppressWarnings("unused")
    void onAddClicked() {
        BusUtils.post(new ListAddActivity.AddEvent());
    }
}
