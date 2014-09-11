package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

import ua.org.cofriends.reades.utils.EventBusUtils;

public class Book extends SugarRecord<Book>{

    @Expose
    @SerializedName("id")
    private final int bookId;

    @Expose
    private final String name;

    @Expose
    private final String fileUrl;

    @Expose
    private final Author author;

    private Book(int bookId, String name, String fileUrl, Author author) {
        this.bookId = bookId;
        this.name = name;
        this.fileUrl = fileUrl;
        this.author = author;
    }

    @SuppressWarnings("unused")
    public Book() {
        this(0, null, null, null);
    }

    public int getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Author getAuthor() {
        return author;
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

    public static class Event extends EventBusUtils.Event<Book> {

        public Event(Book object) {
            super(object);
        }
    }

    public static class SelectedEvent extends Event {

        public SelectedEvent(Book object) {
            super(object);
        }
    }

    public static class LoadedEvent extends Event {

        public LoadedEvent(Book object) {
            super(object);
        }
    }

    public static class ListLoadedEvent extends EventBusUtils.Event<List<Book>> {

        public ListLoadedEvent(List<Book> dictionaries) {
            super(dictionaries);
        }
    }
}
