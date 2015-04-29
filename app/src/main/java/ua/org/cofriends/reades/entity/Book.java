package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.utils.BusUtils;

public class Book extends SugarRecord<Book> implements DownloadService.Loadable {

    @Expose
    @SerializedName("id")
    private int bookId;

    @Expose
    private String name;

    @Expose
    private String fileUrl;

    @Expose
    private final String imageUrl;

    @Expose
    private Author author;

    @Expose
    private Language language;

    @Expose
    private SourceType sourceType = SourceType.LIBRARY; // default source is library

    @Expose
    private FormatType formatType;

    private Book(int bookId, String name, String imageUrl) {
        this.bookId = bookId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @SuppressWarnings("unused")
    public Book() {
        this(0, null, null);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return download URL for API received entity or local path for database entry
     */
    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public String getDownloadUrl() {
        return getFileUrl();
    }

    @Override
    public void setLoadedPath(String path) {
        fileUrl = path;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public FormatType getFormatType() {
        return formatType;
    }

    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }

    public Book meFromDb() {
        Book book = Book.fromDb(getBookId());
        if (book == null) {
            // there is no me in the database
            save();
            return this;
        }
        return book;
    }

    public static Book fromDb(int bookId) {
        List<Book> books = Book.find(Book.class, "BOOK_ID = ?", Integer.toString(bookId));
        if (books.isEmpty()) {
            return null;
        }
        return books.get(0);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", name='" + name + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", author=" + author +
                ", language=" + language +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (bookId != book.bookId) return false;
        return !(name != null ? !name.equals(book.name) : book.name != null);

    }

    @Override
    public int hashCode() {
        int result = bookId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public static class Event extends BusUtils.Event<Book> {

        public Event(Book object) {
            super(object);
        }
    }

    public static class SelectedEvent extends Event {

        public SelectedEvent(Book object) {
            super(object);
        }
    }

    public static class SavedEvent extends Event {

        public SavedEvent(Book object) {
            super(object);
        }
    }

    public static class ListLoadedEvent extends BusUtils.Event<List<Book>> {

        public ListLoadedEvent(List<Book> dictionaries) {
            super(dictionaries);
        }
    }

    public static class DeviceListLoadedEvent extends ListLoadedEvent {

        public DeviceListLoadedEvent(List<Book> dictionaries) {
            super(dictionaries);
        }
    }

    public static class LibraryListLoadedEvent extends ListLoadedEvent {

        public LibraryListLoadedEvent(List<Book> dictionaries) {
            super(dictionaries);
        }
    }

    public static class LoadedEvent extends Event {

        public LoadedEvent(Book object) {
            super(object);
        }
    }

    public enum SourceType {

        LIBRARY(R.string.title_library, R.layout.saved_books_view), DEVICE(R.string.title_device, R.layout.device_books_view);

        private final int titleString;
        private final int viewId;

        SourceType(int titleString, int viewId) {
            this.titleString = titleString;
            this.viewId = viewId;
        }

        public int getTitleString() {
            return titleString;
        }

        public int getViewId() {
            return viewId;
        }
    }

    public enum FormatType {

        TXT(".+\\.txt"), PDF(".+\\.pdf");

        private final String pattern;

        FormatType(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }
}
