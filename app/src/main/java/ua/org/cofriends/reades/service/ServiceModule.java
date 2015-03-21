package ua.org.cofriends.reades.service;

import android.app.Service;

import com.cocosw.undobar.UndoBarController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import ua.org.cofriends.reades.AppModule;
import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.ui.basic.BaseActivity;
import ua.org.cofriends.reades.ui.basic.DrawerToggle;
import ua.org.cofriends.reades.ui.books.BooksActivity;
import ua.org.cofriends.reades.ui.dictionaries.DictionariesActivity;
import ua.org.cofriends.reades.ui.read.PageView;
import ua.org.cofriends.reades.ui.read.ReadActivity;
import ua.org.cofriends.reades.ui.words.WordsDrawerView;

@Module(addsTo = AppModule.class,
injects = {
        SavedBooksService.class
}, library = true, complete = false)
public final class ServiceModule {

    private final Service mService;

    public ServiceModule(Service service) {
        this.mService = service;
    }

    public static ObjectGraph serviceScopeGraph(Service service) {
        return MainApplication.get(service).objectGraph()
                .plus(new ServiceModule(service));
    }
}