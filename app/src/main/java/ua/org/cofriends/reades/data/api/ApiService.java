package ua.org.cofriends.reades.data.api;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;

public interface ApiService {

    /**
     * Requests book list.
     *
     * @param languageId of language to load books written in
     * @param callback   to handle response
     */
    @GET("/languages/{languageId}/books/")
    void listBooks(@Path("languageId") String languageId, Callback<List<Book>> callback);

    /**
     * Requests dictionaries.
     *
     * @param callback to handle response
     */
    @GET("/dictionaries/")
    void listDictionaries(Callback<List<Dictionary>> callback);
}