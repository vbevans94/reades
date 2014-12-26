package ua.org.cofriends.reades;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.ui.activity.BaseActivity;
import ua.org.cofriends.reades.ui.activity.BooksActivity;
import ua.org.cofriends.reades.ui.activity.DictionariesActivity;
import ua.org.cofriends.reades.ui.activity.ReadActivity;
import ua.org.cofriends.reades.ui.fragment.PageFragment;
import ua.org.cofriends.reades.ui.fragment.WordsDrawerFragment;
import ua.org.cofriends.reades.ui.fragment.books.DownloadBooksFragment;
import ua.org.cofriends.reades.ui.fragment.books.SavedBooksFragment;
import ua.org.cofriends.reades.ui.fragment.dictionaries.DownloadDictionariesFragment;
import ua.org.cofriends.reades.ui.fragment.dictionaries.SavedDictionariesFragment;

@Module(injects = {BaseActivity.class
        , DictionariesActivity.class
        , BooksActivity.class
        , ReadActivity.class
        , WordsDrawerFragment.class
        , SavedBooksFragment.class
        , SavedDictionariesFragment.class
        , DownloadBooksFragment.class
        , DownloadDictionariesFragment.class
        , PageFragment.class
}, library = false, complete = false)
public final class AppModule {

    private final MainApplication mainApplication;

    public AppModule(MainApplication application) {
        this.mainApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mainApplication;
    }
}