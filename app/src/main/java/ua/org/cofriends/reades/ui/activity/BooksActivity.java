package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.fragment.books.DownloadBooksFragment;
import ua.org.cofriends.reades.ui.fragment.books.SavedBooksFragment;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends TabbedActivity {

    private static final String EXTRA_DICTIONARY_ID = "extra_dictionary_id";

    public static void start(int dictionaryId, Context context) {
        Bundle extras = BundleUtils.putInt(null, EXTRA_DICTIONARY_ID, dictionaryId);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    Fragment getDownloadFragment() {
        return new DownloadBooksFragment();
    }

    @Override
    Fragment getSavedFragment() {
        return new SavedBooksFragment();
    }

    @SuppressWarnings("unused")
    public void onEvent(Dictionary.SelectedEvent event) {
        // TODO: move to books activity
        Toast.makeText(this, event.getData().getDbUrl(), Toast.LENGTH_LONG).show();
    }
}
