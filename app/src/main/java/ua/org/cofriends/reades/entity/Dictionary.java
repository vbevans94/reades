package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;

import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.ui.adapter.SimpleAdapter;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class Dictionary extends SugarRecord<Dictionary> implements DownloadService.Loadable, SimpleAdapter.Viewable {

    @Expose
    @SerializedName("id")
    private final int dictionaryId;

    @Expose
    private final String name;

    @Expose
    private final String dbUrl;

    private Dictionary(int dictionaryId, String name, String dbUrl) {
        this.dictionaryId = dictionaryId;
        this.name = name;
        this.dbUrl = dbUrl;
    }

    @SuppressWarnings("unused")
    public Dictionary() {
        this(0, null, null);
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    @Override
    public long getItemId() {
        return getDictionaryId();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    @Override
    public String getUrl() {
        return getDbUrl();
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "dictionaryId=" + dictionaryId +
                ", name='" + name + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dictionary that = (Dictionary) o;

        if (dictionaryId != that.dictionaryId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dictionaryId;
    }

    public static class Event extends EventBusUtils.Event<Dictionary> {

        public Event(Dictionary object) {
            super(object);
        }
    }

    public static class SelectedEvent extends Event {

        public SelectedEvent(Dictionary object) {
            super(object);
        }
    }

    public static class SavedEvent extends Event {

        public SavedEvent(Dictionary object) {
            super(object);
        }
    }

    public static class ListLoadedEvent extends EventBusUtils.Event<List<Dictionary>> {

        public ListLoadedEvent(List<Dictionary> dictionaries) {
            super(dictionaries);
        }
    }
}
