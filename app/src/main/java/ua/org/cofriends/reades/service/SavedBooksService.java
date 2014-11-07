package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Author;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.entity.Language;
import ua.org.cofriends.reades.entity.Page;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedBooksService extends IntentService {

    private static final int LOAD_LIST = 0;
    public static final int SAVE = 1;
    public static final int DELETE = 2;
    private static final String EXTRA_TYPE = "extra_type";

    public SavedBooksService() {
        super(SavedBooksService.class.getSimpleName());
    }

    /**
     * Loads books that were saved under given language.
     * @param context to use
     * @param language to search books with
     */
    public static void loadListByLanguage(Context context, Language language) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Language.class, language))
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    /**
     * Does action upon book in the database.
     * @param context to use
     * @param book to save
     * @param action to do. One of {@link #DELETE} or {@link #SAVE}.
     */
    public static void actUpon(Context context, Book book, int action) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Book.class, book))
                .putExtra(EXTRA_TYPE, action));
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE: {
                // fetch book and dictionary ID from params bundle
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras());

                // link book with existing in the database language
                book.setLanguage(book.getLanguage().meFromDb());

                // save book author if it's not yet or just link with existing in the database
                book.setAuthor(book.getAuthor().meFromDb());

                book.save();
                loadBooks(intent);
                break;
            }

            case DELETE: {
                // fetch book and dictionary ID from params bundle
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras()).meFromDb();

                deleteBook(book);

                // load up to date book list
                loadBooks(intent);
                break;
            }

            case LOAD_LIST:
                loadBooks(intent);
                break;
        }
    }

    /**
     * Deletes book's pages and book's itself record from the database.
     * @param book to delete
     */
    public static void deleteBook(Book book) {
        // delete book pages if there were some saved
        List<Page> bookPages = Page.find(Page.class, "book = ?", Long.toString(book.getId()));
        if (!bookPages.isEmpty()) {
            for (Page page : bookPages) {
                page.delete();
            }
        }

        // delete book itself
        book.delete();
    }

    private void loadBooks(Intent intent) {
        Language language = BundleUtils.fetchFromBundle(Language.class, intent.getExtras()).meFromDb();
        List<Book> books = Book.find(Book.class, "language = ?", Long.toString(language.getId()));
        BusUtils.post(new Book.ListLoadedEvent(books));
    }
}
