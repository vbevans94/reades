package ua.org.cofriends.reades.entity;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class Book extends SugarRecord<Book> implements DownloadService.Loadable
        , SimpleAdapter.Viewable, BundleUtils.Persistable {

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
    private Dictionary dictionary;

    /**
     * To store value from {@link #getId()} which is not serialized/deserialized.
     * See {@link #getId()}
     */
    @Expose
    @Ignore
    private long persistedId;

    private Book(int bookId, String name, String imageUrl) {
        this.bookId = bookId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @SuppressWarnings("unused")
    public Book() {
        this(0, null, null);
    }

    @Override
    public Bundle persistIn(Bundle bundle) {
        getId(); // trigger setting this.persistedId
        if (dictionary != null) {
            dictionary.getId(); // triggers setting persistedId of dictionary
        }
        return BundleUtils.writeNoStrategies(Book.class, this, bundle);
    }

    /**
     * @return persisted id or {@code super.getId()} if not set
     */
    @Override
    public Long getId() {
        if (persistedId == 0l && super.getId() != null) {
            persistedId = super.getId();
        }
        return persistedId;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getBookId() {
        return bookId;
    }

    @Override
    public long getItemId() {
        return getBookId();
    }

    @Override
    public String getItemName() {
        return getName();
    }

    @Override
    public String getItemDetails() {
        return getAuthor().getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
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
    public void setLoadedPath(String url) {
        fileUrl = url;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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
