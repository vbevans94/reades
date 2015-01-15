package ua.org.cofriends.reades;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.books.BooksActivity;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.ui.read.ReadActivity;
import ua.org.cofriends.reades.utils.GoogleApi;

@Module(library = true)
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