package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Author;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedBooksService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final int DELETE = 2;
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_DICTIONARY_ID = "extra_dictionary_id";
    private static final String EXTRA_BOOK_ID = "extra_book_id";

    public SavedBooksService() {
        super(SavedBooksService.class.getSimpleName());
    }

    /**
     * Loads books that were saved under given dictionary.
     * @param context to use
     * @param dictionary to search books with
     */
    public static void loadListByDictionary(Context context, Dictionary dictionary) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_DICTIONARY_ID, dictionary.getId())
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    /**
     * Loads all books on the device.
     * @param context to use
     */
    public static void loadAll(Context context) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    /**
     * Saves book into the database.
     * @param context to use
     * @param book to save
     * @param dictionary to save under
     */
    public static void saveWithDictionary(Context context, Book book, Dictionary dictionary) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Book.class, book))
                .putExtra(EXTRA_DICTIONARY_ID, dictionary.getId())
                .putExtra(EXTRA_TYPE, SAVE));
    }

    /**
     * Deletes book from the database.
     * @param context to use
     * @param book to save
     */
    public static void delete(Context context, Book book) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_BOOK_ID, book.getId())
                .putExtra(EXTRA_DICTIONARY_ID, book.getDictionary().getId())
                .putExtra(EXTRA_TYPE, DELETE));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE: {
                // fetch book and dictionary ID from params bundle
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras());
                long dictionaryId = intent.getLongExtra(EXTRA_DICTIONARY_ID, 0l);

                // link book with existing in the database dictionary
                Dictionary dictionary = Dictionary.findById(Dictionary.class, dictionaryId);
                book.setDictionary(dictionary);

                // save book author if it's not yet or just link with existing in the database
                Author author = book.getAuthor();
                if (author != null) {
                    // first check if author with the same author ID exists in the database
                    List<Author> savedAuthors = Author.find(Author.class, "AUTHOR_ID = ?", Integer.toString(author.getAuthorId()));
                    if (savedAuthors.isEmpty()) {
                        // author of this book hasn't been saved in the database yet, save it
                        author.save();
                    } else {
                        // this book's author is already saved, just link it
                        book.setAuthor(savedAuthors.get(0));
                    }
                }
                book.save();
                loadBooks(intent);
                break;
            }

            case DELETE: {
                long bookId = intent.getLongExtra(EXTRA_BOOK_ID, 0l);
                Book book = Book.findById(Book.class, bookId);
                book.delete();
                loadBooks(intent);
                break;
            }

            case LOAD_LIST:
                loadBooks(intent);
                break;
        }
    }

    private void loadBooks(Intent intent) {
        long dictionaryId = intent.getLongExtra(EXTRA_DICTIONARY_ID, 0l);
        List<Book> books = dictionaryId != 0l
                ? Book.find(Book.class, "dictionary = ?", Long.toString(dictionaryId))
                : Book.listAll(Book.class);
        BusUtils.post(new Book.ListLoadedEvent(books));
    }
}
