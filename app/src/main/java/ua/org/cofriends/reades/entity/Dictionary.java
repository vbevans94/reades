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

public class Dictionary extends SugarRecord<Dictionary> implements DownloadService.Loadable
        , SimpleAdapter.Viewable, BundleUtils.Persistable {

    @Expose
    @SerializedName("id")
    private final int dictionaryId;

    @Expose
    private Language fromLanguage;

    @Expose
    private Language toLanguage;

    @Expose
    private String dbUrl;

    /**
     * To store value from {@link #getId()} which is not serialized/deserialized.
     * See {@link #getId()}
     */
    @Expose
    @Ignore
    private long persistedId;

    private Dictionary(int dictionaryId, String dbUrl) {
        this.dictionaryId = dictionaryId;
        this.dbUrl = dbUrl;
    }

    @SuppressWarnings("unused")
    public Dictionary() {
        this(0, null);
    }

    @Override
    public Bundle persistIn(Bundle bundle) {
        persistedId = getId();
        if (fromLanguage != null) {
            fromLanguage.getId();
        }
        if (toLanguage != null) {
            toLanguage.getId();
        }
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
        return String.format("%s - %s", fromLanguage.getName(), toLanguage.getName());
    }

    @Override
    public String getImageUrl() {
        return toLanguage.getImageUrl();
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

    public Language getFromLanguage() {
        return fromLanguage;
    }

    public Language getToLanguage() {
        return toLanguage;
    }

    public void setFromLanguage(Language fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public void setToLanguage(Language toLanguage) {
        this.toLanguage = toLanguage;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "dictionaryId=" + dictionaryId +
                ", fromLanguage=" + fromLanguage +
                ", toLanguage=" + toLanguage +
                ", dbUrl='" + dbUrl + '\'' +
                ", persistedId=" + persistedId +
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
        return String.format("%s%s-%s", getDbUrl(), fromLanguage.getCode(), toLanguage.getCode());
    }

    public static class Event extends BusUtils.Event<Dictionary> {

        public Event(Dictionary object) {
            super(object);
        }
    }

    public static class SelectedEvent extends Event {

        public SelectedEvent(Dictionary object) {
            super(object);
        }
    }

    public static class ListLoadedEvent extends BusUtils.Event<List<Dictionary>> {

        public ListLoadedEvent(List<Dictionary> dictionaries) {
            super(dictionaries);
        }
    }
}
