package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.ui.fragment.books.DownloadBooksFragment;
import ua.org.cofriends.reades.ui.fragment.books.SavedBooksFragment;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends TabbedActivity {

    public static final String EXTRA_DICTIONARY_ID = "extra_dictionary_id";

    public static void start(int dictionaryId, Context context) {
        Bundle extras = BundleUtils.putInt(null, EXTRA_DICTIONARY_ID, dictionaryId);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    Fragment getDownloadFragment() {
        return addExtras(new DownloadBooksFragment());
    }

    @Override
    Fragment getSavedFragment() {
        return addExtras(new SavedBooksFragment());
    }

    @SuppressWarnings("unused")
    public void onEvent(Book.SavedEvent event) {
        // TODO: move to read activity
        Toast.makeText(this, event.getData().getFileUrl(), Toast.LENGTH_LONG).show();
    }

    public Fragment addExtras(Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }
}
