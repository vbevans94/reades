package ua.org.cofriends.reades.ui.words;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.service.SavedWordsService;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.basic.BaseFrameLayout;
import ua.org.cofriends.reades.ui.basic.DrawerToggle;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;
import ua.org.cofriends.reades.ui.basic.tools.FillTransform;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.GoogleApi;

public class WordsDrawerView extends BaseFrameLayout {

    @InjectView(R.id.list_words)
    ListView mListWords;

    @InjectView(R.id.text_account_name)
    TextView mTextAccountName;

    @InjectView(R.id.image_account)
    ImageView mImageAccount;

    @Inject
    GoogleApi mGoogleApi;

    @Inject
    DrawerToggle mDrawerToggle;

    public WordsDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.words_drawer_view, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mListWords.setEmptyView(findViewById(R.id.text_empty));
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person);
        icon = new FillTransform(getResources().getColor(R.color.white)).transform(icon);
        icon = new CircleTransform().transform(icon);
        mImageAccount.setImageBitmap(icon);

        post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mGoogleApi.connect();

        if (mListWords.getAdapter() == null) {
            SavedWordsService.loadList(getContext());
        }
    }

    @OnClick(R.id.image_account)
    @SuppressWarnings("unused")
    void onAccountImageClicked() {
        mGoogleApi.manualConnect();
    }

    @OnClick(R.id.text_account_name)
    @SuppressWarnings("unused")
    void onAccountNameClicked() {
        mGoogleApi.manualConnect();
    }

    @OnClick(R.id.layout_home)
    @SuppressWarnings("unused")
    void onHomeClicked() {
        BusUtils.post(new HomeEvent());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mGoogleApi.connect();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Word.ListLoadedEvent event) {
        mListWords.setAdapter(new WordsAdapter(getContext(), event.getData()));
    }

    @OnItemClick(R.id.list_words)
    @SuppressWarnings("unused")
    public void onWordClicked(int position) {
        Word word = (Word) mListWords.getItemAtPosition(position);
        DefinitionDialogFactory.show(getContext(), word);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Word.RemoveEvent event) {
        SavedWordsService.delete(getContext(), event.getData());
    }

    @SuppressWarnings("unused")
    public void onEvent(GoogleApi.ConnectedEvent event) {
        GoogleApiClient client = event.getData();
        mGoogleApi.loadImage(mImageAccount);
        mTextAccountName.setText(Plus.AccountApi.getAccountName(client));
    }

    public static class HomeEvent {}
}
