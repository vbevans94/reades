package ua.org.cofriends.reades.ui.basic.tools;

import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder {

    public BaseViewHolder(View view) {
        ButterKnife.inject(this, view);
        view.setTag(this);
    }
}
