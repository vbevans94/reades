package ua.org.cofriends.reades.ui.words;

import android.content.Context;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.service.SavedWordsService;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.basic.DrawerToggle;
import ua.org.cofriends.reades.ui.basic.tools.CircleTransform;
import ua.org.cofriends.reades.ui.basic.tools.FillTransform;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.GoogleApi;

public class WordsDrawerView extends LinearLayout {

    @InjectView(R.id.list_words)
    ListView listWords;

    @InjectView(R.id.text_account_name)
    TextView textAccountName;

    @InjectView(R.id.image_account)
    ImageView imageAccount;

    @Inject
    GoogleApi googleApi;

    @Inject
    DrawerToggle drawerToggle;

    private final WordsAdapter adapter;

    public WordsDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.words_drawer_view, this);

        adapter = new WordsAdapter(context);

        setBackgroundResource(R.color.white);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        listWords.setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        BaseActivity.get(getContext()).inject(this);

        listWords.setEmptyView(findViewById(R.id.text_empty));
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person);
        icon = new FillTransform(getResources().getColor(R.color.white)).transform(icon);
        icon = new CircleTransform().transform(icon);
        imageAccount.setImageBitmap(icon);

        post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        googleApi.connect();

        if (listWords.getAdapter() == null) {
            SavedWordsService.loadList(getContext());
        }

        BusUtils.register(this);
    }

    @OnClick(R.id.image_account)
    @SuppressWarnings("unused")
    void onAccountImageClicked() {
        googleApi.manualConnect();
    }

    @OnClick(R.id.text_account_name)
    @SuppressWarnings("unused")
    void onAccountNameClicked() {
        googleApi.manualConnect();
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
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        googleApi.disconnect();
        BusUtils.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onWordListLoaded(Word.ListLoadedEvent event) {
        adapter.replaceWith(event.getData());
    }

    @OnItemClick(R.id.list_words)
    @SuppressWarnings("unused")
    public void onWordClicked(int position) {
        Word word = (Word) listWords.getItemAtPosition(position);
        DefinitionDialogFactory.show(getContext(), word);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onWordRemove(Word.RemoveEvent event) {
        SavedWordsService.delete(getContext(), event.getData());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onGoogleConnected(GoogleApi.ConnectedEvent event) {
        GoogleApiClient client = event.getData();
        googleApi.loadImage(imageAccount);
        textAccountName.setText(Plus.AccountApi.getAccountName(client));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onConnectionFailed(GoogleApi.ConnectionFailedEvent event) {
        try {
            BaseActivity.get(getContext()).startIntentSenderForResult(event.getData().getResolution().getIntentSender(),
                    GoogleApi.RC_SIGN_IN, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            googleApi.connect();
        }
    }

    public static class HomeEvent {}
}
