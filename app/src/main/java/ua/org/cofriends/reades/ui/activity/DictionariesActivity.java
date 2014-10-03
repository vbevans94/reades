package ua.org.cofriends.reades.ui.activity;

import android.support.v4.app.Fragment;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.fragment.dictionaries.DownloadDictionariesFragment;
import ua.org.cofriends.reades.ui.fragment.dictionaries.SavedDictionariesFragment;

public class DictionariesActivity extends ListAddActivity {

    @SuppressWarnings("unused")
    public void onEvent(Dictionary.SelectedEvent event) {
        BooksActivity.start(event.getData(), this);
    }

    @Override
    Fragment getDownloadFragment() {
        return new DownloadDictionariesFragment();
    }

    @Override
    Fragment getSavedFragment() {
        return new SavedDictionariesFragment();
    }
}
