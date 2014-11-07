package ua.org.cofriends.reades.service;

import android.content.Context;

import ua.org.cofriends.reades.entity.Book;

public class BookDownloadService extends DownloadService {

    public static void start(Context context, Loadable loadable) {
        DownloadService.start(context, loadable, BookDownloadService.class);
    }

    @Override
    public void onLoaded(Loadable loadable) {
        if (loadable instanceof Book) {
            SavedBooksService.actUpon(this, (Book) loadable, SavedBooksService.SAVE);
        }
    }
}
