package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.fragment.books.DownloadBooksFragment;
import ua.org.cofriends.reades.ui.fragment.books.SavedBooksFragment;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends TabbedActivity {

    public static void start(Dictionary dictionary, Context context) {
        Bundle extras = BundleUtils.writeObject(Dictionary.class, dictionary);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    Fragment getDownloadFragment() {
        return withExtrasAsArgs(new DownloadBooksFragment());
    }

    @Override
    Fragment getSavedFragment() {
        return withExtrasAsArgs(new SavedBooksFragment());
    }

    @SuppressWarnings("unused")
    public void onEvent(Book.SavedEvent event) {
        // TODO: move to read activity
        Toast.makeText(this, event.getData().getFileUrl(), Toast.LENGTH_LONG).show();
    }

    public Fragment withExtrasAsArgs(Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }
}
