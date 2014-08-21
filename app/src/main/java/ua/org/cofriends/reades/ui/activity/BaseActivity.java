package ua.org.cofriends.reades.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import butterknife.ButterKnife;
import ua.org.cofriends.reades.utils.Logger;

public class BaseActivity extends ActionBarActivity {

    protected final String mTag = Logger.makeLogTag(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
    }
}
