package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedBooksService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_DICTIONARY_ID = "extra_dictionary_id";

    public SavedBooksService() {
        super(SavedBooksService.class.getSimpleName());
    }

    public static void loadListByDictionary(Context context, Dictionary dictionary) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_DICTIONARY_ID, dictionary.getId())
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    public static void save(Context context, Book book) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Book.class, book))
                .putExtra(EXTRA_TYPE, SAVE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case LOAD_LIST:
                long dictionaryId = intent.getLongExtra(EXTRA_DICTIONARY_ID, 0l);
                List<Book> books = dictionaryId != 0l
                        ? Book.find(Book.class, "dictionary = ?", Long.toString(dictionaryId))
                        : Book.listAll(Book.class);
                EventBusUtils.getBus().post(new Book.ListLoadedEvent(books));
                break;

            case SAVE:
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras());
                Dictionary dictionary = Dictionary.findById(Dictionary.class, book.getDictionary().getId());
                book.setDictionary(dictionary);
                book.save();
                EventBusUtils.getBus().post(new Book.SavedEvent(book));
                break;
        }
    }
}
