package ua.org.cofriends.reades.entity;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import ua.org.cofriends.reades.utils.BundleUtils;

public class Language extends SugarRecord<Language> implements BundleUtils.Persistable {

    @Expose
    @SerializedName("id")
    private final int languageId;

    @Expose
    private final String code;

    @Expose
    private final String name;

    @Expose
    private final String imageUrl;

    @Expose
    private long persistedId;

    private Language(int languageId, String code, String name, String imageUrl) {
        this.languageId = languageId;
        this.code = code;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * For Gson and Sugar.
     */
    @SuppressWarnings("unused")
    public Language() {
        this(0, null, null, null);
    }

    @Override
    public Bundle persistIn(Bundle bundle) {
        getId(); // trigger setting id of me
        return BundleUtils.writeNoStrategies(Language.class, this, bundle);
    }

    @Override
    public Long getId() {
        if (persistedId == 0l && super.getId() != null) {
            persistedId = super.getId();
        }
        return persistedId;
    }

    public int getLanguageId() {
        return languageId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Language{" +
                "languageId=" + languageId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        if (languageId != language.languageId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return languageId;
    }
}
