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
import ua.org.cofriends.reades.utils.EventBusUtils;

public class Dictionary extends SugarRecord<Dictionary> implements DownloadService.Loadable
        , SimpleAdapter.Viewable, BundleUtils.Persistable {

    @Expose
    @SerializedName("id")
    private final int dictionaryId;

    @Expose
    private final String name;

    @Expose
    private String dbUrl;

    /**
     * To store value from {@link #getId()} which is not serialized/deserialized.
     * See {@link #getId()}
     */
    @Expose
    @Ignore
    private long persistedId;

    private Dictionary(int dictionaryId, String name, String dbUrl) {
        this.dictionaryId = dictionaryId;
        this.name = name;
        this.dbUrl = dbUrl;
    }

    @SuppressWarnings("unused")
    public Dictionary() {
        this(0, null, null);
    }

    @Override
    public Bundle persistIn(Bundle bundle) {
        persistedId = getId();
        return BundleUtils.writeNoStrategies(Dictionary.class, this, bundle);
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

    /**
     * @return dictionary ID returned by API, so by it we can work with API
     */
    public int getDictionaryId() {
        return dictionaryId;
    }

    /**
     * @return id to use in the adapters
     */
    @Override
    public long getItemId() {
        return getDictionaryId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getItemName() {
        return getName();
    }

    /**
     * @return download URL for API received entity or local path for database entry
     */
    public String getDbUrl() {
        return dbUrl;
    }

    @Override
    public String getDownloadUrl() {
        return getDbUrl();
    }

    @Override
    public void setLoadedPath(String url) {
        // we will write into the database the local path
        dbUrl = url;
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

    public String getDbConfigPath() {
        return getDbUrl() + getName();
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

    public static class ListLoadedEvent extends EventBusUtils.Event<List<Dictionary>> {

        public ListLoadedEvent(List<Dictionary> dictionaries) {
            super(dictionaries);
        }
    }
}
