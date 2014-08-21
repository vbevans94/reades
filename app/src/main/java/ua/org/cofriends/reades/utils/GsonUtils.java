package ua.org.cofriends.reades.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {

    private final static Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static <T> T fromJson(String jsonText, Class<T> clazz) {
        return GSON.fromJson(jsonText, clazz);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static Gson gson() {
        return GSON;
    }
}
