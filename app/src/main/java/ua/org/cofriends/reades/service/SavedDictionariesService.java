package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.ZipUtils;

public class SavedDictionariesService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final int DELETE = 2;
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_DICTIONARY_ID = "extra_dictionary_id";

    public SavedDictionariesService() {
        super(SavedDictionariesService.class.getSimpleName());
    }

    public static void loadList(Context context) {
        context.startService(new Intent(context, SavedDictionariesService.class)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    public static void save(Context context, Dictionary dictionary) {
        context.startService(new Intent(context, SavedDictionariesService.class)
                .putExtra(EXTRA_TYPE, SAVE)
                .putExtras(BundleUtils.writeObject(Dictionary.class, dictionary)));
    }

    public static void delete(Context context, Dictionary dictionary) {
        context.startService(new Intent(context, SavedDictionariesService.class)
                .putExtra(EXTRA_TYPE, DELETE)
                .putExtra(EXTRA_DICTIONARY_ID, dictionary.getId()));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE: {
                Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, intent.getExtras());

                // extract zip to private data or SD card if there is no room
                String newPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
                dictionary.setLoadedPath(ZipUtils.unzip(dictionary.getDbUrl(), newPath));

                // save to the database
                dictionary.save();
                loadAll();
                break;
            }

            case DELETE: {
                long dictionaryId = intent.getLongExtra(EXTRA_DICTIONARY_ID, 0l);
                if (dictionaryId != 0l) {
                    Dictionary dictionary = Dictionary.findById(Dictionary.class, dictionaryId);
                    // delete all books with this dictionary
                    List<Book> books = Book.find(Book.class, "dictionary = ?", Long.toString(dictionaryId));
                    for (Book book : books) {
                        book.delete();
                    }
                    // delete dictionary itself
                    dictionary.delete();
                    loadAll();
                }


                break;
            }

            case LOAD_LIST:
                loadAll();
                break;
        }
    }

    private void loadAll() {
        // retrieve updated list of all dictionaries from the database
        List<Dictionary> dictionaries = Dictionary.listAll(Dictionary.class);
        BusUtils.post(new Dictionary.ListLoadedEvent(dictionaries));
    }
}
