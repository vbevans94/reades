package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.local.OpenFileController;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.ListAddActivity;
import ua.org.cofriends.reades.ui.read.ReadActivity;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends ListAddActivity {

    @InjectView(R.id.pager)
    ViewPager pager;

    @Inject
    OpenFileController openFileController;

    @Inject
    Picasso picasso;

    public static void start(Dictionary dictionary, Context context) {
        Bundle extras = BundleUtils.writeObject(Dictionary.class, dictionary);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
        SavedDictionariesService.setCurrentDictionary(dictionary);

        ButterKnife.inject(this);

        pager.setAdapter(new BookPagerAdapter(this));

        setTitle(getString(R.string.title_languaged_books, dictionary.getFromLanguage().getName()));
        // TODO: fix it
        /*picasso.load(dictionary.getFromLanguage().getImageUrl())
                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable logo = new BitmapDrawable(getResources(), bitmap);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setLogo(logo);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });*/
    }

    @Override
    protected int getDownloadViewId() {
        return R.layout.download_books_view;
    }

    @Override
    protected int getSavedViewId() {
        return R.layout.saved_books_activity;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onBookSelected(Book.SelectedEvent event) {
        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
        ReadActivity.start(event.getData(), dictionary, this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void oтBookOpen(DeviceBooksView.OpenBookEvent event) {
        openFileController.showOpenDialog(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        openFileController.processResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}