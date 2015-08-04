package ua.org.cofriends.reades.data.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import ua.org.cofriends.reades.BuildConfig;

@Module(library = true, complete = false)
public class ApiModule {

    public static final String PRODUCTION_API_URL = "http://reades.herokuapp.com/api";

    @Provides
    @Singleton
    ApiService provideApiService(RestAdapter adapter) {
        return adapter.create(ApiService.class);
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Gson gson, Endpoint endpoint,
                                   Client client, @ApiAuth RequestInterceptor authenticator) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(authenticator)
                .setConverter(new GsonConverter(gson))
                .setEndpoint(endpoint)
                .build();
    }

    @Provides
    @Singleton
    @ApiAuth
    RequestInterceptor provideAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Token " + BuildConfig.AUTH_TOKEN);
            }
        };
    }

    @Provides
    @Singleton
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(PRODUCTION_API_URL);
    }

    @Provides
    @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides
    @Singleton
    Gson provideAutoParcelGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }
}
