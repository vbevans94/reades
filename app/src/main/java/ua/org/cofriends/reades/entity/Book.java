package ua.org.cofriends.reades.entity;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.ui.adapter.BookAdapter;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class Book extends SugarRecord<Book> implements DownloadService.Loadable {

    @Expose
    @SerializedName("id")
    private final int bookId;

    @Expose
    private final String name;

    @Expose
    private String fileUrl;

    @Expose
    private final String imageUrl;

    @Expose
    private Author author;

    @Expose
    private Language language;

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

    @Override
    public String getName() {
        return name;
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

    public Book meFromDb() {
        List<Book> books = Book.find(Book.class, "BOOK_ID = ?", Integer.toString(getBookId()));
        if (books.isEmpty()) {
            save();
            return this;
        }
        return books.get(0);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", name='" + name + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book that = (Book) o;

        if (bookId != that.bookId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bookId;
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

    public static class ListLoadedEvent extends BusUtils.Event<List<Book>> {

        public ListLoadedEvent(List<Book> dictionaries) {
            super(dictionaries);
        }
    }
}
