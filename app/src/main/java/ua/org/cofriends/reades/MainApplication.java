package ua.org.cofriends.reades;

import com.orm.SugarApp;

import ua.org.cofriends.reades.utils.LocalStorage;

public class MainApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalStorage.INSTANCE.init(this);
    }
}
