package ua.org.cofriends.reades.service.book;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import javax.inject.Inject;

import ua.org.cofriends.reades.MainApplication;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Language;
import ua.org.cofriends.reades.entity.Page;
import ua.org.cofriends.reades.local.BookAdapterFactory;
import ua.org.cofriends.reades.service.ServiceModule;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.Logger;

public class SavedBooksService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int LOAD_BY_ID = 1;
    public static final int DELETE = 2;
    public static final int SAVE = 3;
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_SOURCE = "extra_source";
    private static final String TAG = Logger.makeLogTag(SavedBooksService.class);

    @Inject
    BookAdapterFactory bookAdapterFactory;

    public SavedBooksService() {
        super(SavedBooksService.class.getSimpleName());

        setIntentRedelivery(true);
    }

    /**
     * Loads books that were saved under given language from given source.
     *
     * @param context    to use
     * @param language   to search books with
     * @param sourceType of the books
     */
    public static void loadList(Context context, Language language, Book.SourceType sourceType) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Language.class, language))
                .putExtra(EXTRA_SOURCE, sourceType)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    /**
     * Loads single book by its id.
     *
     * @param bookId  to search for in the database
     * @param context to use
     */
    public static void loadById(int bookId, Context context) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtra(EXTRA_TYPE, LOAD_BY_ID)
                .putExtra(EXTRA_ID, bookId));
    }

    /**
     * Does action upon book in the database.
     *
     * @param context to use
     * @param book    to save
     * @param action  to do upon the book
     */
    public static void actUpon(Context context, Book book, int action) {
        context.startService(new Intent(context, SavedBooksService.class)
                .putExtras(BundleUtils.writeObject(Book.class, book))
                .putExtra(EXTRA_TYPE, action));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ServiceModule.serviceScopeGraph(this).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE: {
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras());

                saveBook(book);

                BusUtils.post(new Book.DoneEvent(book));

                break;
            }

            case DELETE: {
                // fetch book and dictionary ID from params bundle
                Book book = BundleUtils.fetchFromBundle(Book.class, intent.getExtras()).meFromDb();

                deleteBook(book);

                // tell the world we are done
                BusUtils.post(new Book.DoneEvent(book));

                break;
            }

            case LOAD_LIST:
                loadBooks(intent);
                break;

            case LOAD_BY_ID:
                int bookId = intent.getIntExtra(EXTRA_ID, 0);
                loadById(bookId);
                break;
        }
    }

    /**
     * Deletes book's pages and book's itself record from the database.
     *
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

    /**
     * Saves book to the database.
     *
     * @param book to save
     */
    private void saveBook(Book book) {
        // link book with existing in the database language

        Logger.d(TAG, book.toString());

        BookAdapterFactory.BookAdapter adapter = bookAdapterFactory.adapterFor(book.getFormatType());
        adapter.adapt(this, book);

        book.setLanguage(book.getLanguage().meFromDb());

        if (book.getSourceType() == Book.SourceType.LIBRARY) {
            // author is present only if a book is loaded from the API server
            book.setAuthor(book.getAuthor().meFromDb());
        }

        book.save();
    }

    private void loadBooks(Intent intent) {
        Language language = BundleUtils.fetchFromBundle(Language.class, intent.getExtras()).meFromDb();
        Book.SourceType sourceType = (Book.SourceType) intent.getExtras().getSerializable(EXTRA_SOURCE);
        List<Book> books = Book.find(Book.class, "language = ? and SOURCE_TYPE = ?", Long.toString(language.getId()), sourceType.toString());
        Book.ListLoadedEvent event = sourceType == Book.SourceType.DEVICE
                ? new Book.DeviceListLoadedEvent(books)
                : new Book.LibraryListLoadedEvent(books);
        BusUtils.postToUi(event);
    }

    private void loadById(int bookId) {
        Book book = Book.fromDb(bookId);
        BusUtils.postToUi(new Book.LoadedEvent(book));
    }
}
