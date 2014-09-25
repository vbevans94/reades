package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

import ua.org.cofriends.reades.utils.BusUtils;

public class Author extends SugarRecord<Author>{

    @Expose
    @SerializedName("id")
    private final int authorId;

    @Expose
    private final String name;

    private Author(int authorId, String name) {
        this.authorId = authorId;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public Author() {
        this(0, null);
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author that = (Author) o;

        if (authorId != that.authorId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return authorId;
    }

    public static class Event extends BusUtils.Event<Author> {

        public Event(Author object) {
            super(object);
        }
    }

    public static class SelectedEvent extends Event {

        public SelectedEvent(Author object) {
            super(object);
        }
    }

    public static class LoadedEvent extends Event {

        public LoadedEvent(Author object) {
            super(object);
        }
    }

    public static class ListLoadedEvent extends BusUtils.Event<List<Author>> {

        public ListLoadedEvent(List<Author> dictionaries) {
            super(dictionaries);
        }
    }
}
