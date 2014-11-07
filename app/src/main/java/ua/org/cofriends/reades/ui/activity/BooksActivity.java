package ua.org.cofriends.reades.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.fragment.books.DownloadBooksFragment;
import ua.org.cofriends.reades.ui.fragment.books.SavedBooksFragment;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends ListAddActivity {

    public static void start(Dictionary dictionary, Context context) {
        Bundle extras = BundleUtils.writeObject(Dictionary.class, dictionary);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    DialogFragment getDownloadFragment() {
        return (DialogFragment) withExtrasAsArgs(new DownloadBooksFragment());
    }

    @Override
    Fragment getSavedFragment() {
        return withExtrasAsArgs(new SavedBooksFragment());
    }

    @SuppressWarnings("unused")
    public void onEvent(Book.SelectedEvent event) {
        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
        ReadActivity.start(event.getData(), dictionary, this);
    }

    public Fragment withExtrasAsArgs(Fragment fragment) {
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }
}
