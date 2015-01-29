package ua.org.cofriends.reades;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.org.cofriends.reades.data.DataModule;

@Module(includes = DataModule.class)
public final class AppModule {

    private final MainApplication mApplication;

    public AppModule(MainApplication application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }
}