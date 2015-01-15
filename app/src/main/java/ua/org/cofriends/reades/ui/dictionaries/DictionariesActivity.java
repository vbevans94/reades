package ua.org.cofriends.reades.ui.dictionaries;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.books.BooksActivity;
import ua.org.cofriends.reades.ui.basic.ListAddActivity;

public class DictionariesActivity extends ListAddActivity {

    @SuppressWarnings("unused")
    public void onEvent(Dictionary.SelectedEvent event) {
        BooksActivity.start(event.getData(), this);
    }

    @Override
    protected int getDownloadViewId() {
        return R.layout.download_dictionaries_view;
    }

    @Override
    protected int getSavedViewId() {
        return R.layout.saved_dictionaries_view;
    }
}
