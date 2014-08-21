package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;

public class Dictionary {

    @Expose
    private final int id;

    @Expose
    private final String name;

    @Expose
    private final String dbUrl;

    private Dictionary(int id, String name, String dbUrl) {
        this.id = id;
        this.name = name;
        this.dbUrl = dbUrl;
    }

    private Dictionary() {
        this(0, null, null);
    }

    public int getId() {
        return id;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                '}';
    }
}
