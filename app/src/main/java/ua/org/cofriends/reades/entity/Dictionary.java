package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Dictionary extends SugarRecord<Dictionary>{

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

    public String getName() {
        return name;
    }

    public String getDbUrl() {
        return dbUrl;
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
}
