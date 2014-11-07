package ua.org.cofriends.reades.service;

import android.content.Context;

import ua.org.cofriends.reades.entity.Dictionary;

public class DictionaryDownloadService extends DownloadService {

    public static void start(Context context, Loadable loadable) {
        DownloadService.start(context, loadable, DictionaryDownloadService.class);
    }

    @Override
    public void onLoaded(Loadable loadable) {
        if (loadable instanceof Dictionary) {
            SavedDictionariesService.actUpon(this, (Dictionary) loadable, SavedBooksService.SAVE);
        }
    }
}
