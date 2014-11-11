package ua.org.cofriends.reades.service.dictionary;

import android.content.Context;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.book.SavedBooksService;

public class DictionaryDownloadService extends DownloadService {

    public static void start(Context context, Loadable loadable) {
        DownloadService.start(context, loadable, DictionaryDownloadService.class);
    }

    @Override
    public void onLoaded(Loadable loadable) {
        if (loadable instanceof Dictionary) {
            SavedDictionariesService.actUpon(this, (Dictionary) loadable, SavedDictionariesService.SAVE);
        }
    }
}
