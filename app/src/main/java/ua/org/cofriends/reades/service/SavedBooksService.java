package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedBooksService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final String EXTRA_TYPE = "extra_type";

    public SavedBooksService() {
        super(SavedBooksService.class.getSimpleName());
    }

    public static void loadList(Context context) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    public static void save(Context context, Book book) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeToBundle(book))
                .putExtra(EXTRA_TYPE, SAVE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case LOAD_LIST:
                List<Book> books = Book.listAll(Book.class);
                EventBusUtils.getBus().post(new Book.ListLoadedEvent(books));
                break;

            case SAVE:
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras());
                book.save();
                EventBusUtils.getBus().post(new Book.SavedEvent(book));
                break;
        }
    }
}
