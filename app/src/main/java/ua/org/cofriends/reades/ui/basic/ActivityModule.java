package ua.org.cofriends.reades.ui.basic;

import com.cocosw.undobar.UndoBarController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.AppModule;
import ua.org.cofriends.reades.ui.books.BooksActivity;
import ua.org.cofriends.reades.ui.books.DownloadBooksView;
import ua.org.cofriends.reades.ui.books.SavedBooksView;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.ui.dictionaries.DownloadDictionariesView;
import ua.org.cofriends.reades.ui.dictionaries.SavedDictionariesView;
import ua.org.cofriends.reades.ui.read.PageView;
import ua.org.cofriends.reades.ui.read.ReadActivity;
import ua.org.cofriends.reades.ui.words.WordsDrawerView;

@Module(library = true, addsTo = AppModule.class,
injects = {
        BaseActivity.class,
        DictionariesActivity.class,
        BooksActivity.class,
        ReadActivity.class,
        PageView.class,
        WordsDrawerView.class,
        SavedDictionariesView.class,
        SavedBooksView.class,
        DownloadBooksView.class,
        DownloadDictionariesView.class
}, complete = false)
public final class ActivityModule {

    private final BaseActivity baseActivity;

    public ActivityModule(BaseActivity activity) {
        this.baseActivity = activity;
    }

    @Provides
    @Singleton
    DrawerToggle provideDrawerToggle() {
        return DrawerToggle.fromActivity(baseActivity);
    }

    @Provides
    @Singleton
    UndoBarController.UndoBar provideUndoBar() {
        return new UndoBarController.UndoBar(baseActivity)
                .style(UndoBarController.UNDOSTYLE);
    }
}