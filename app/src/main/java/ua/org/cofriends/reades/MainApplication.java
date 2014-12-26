package ua.org.cofriends.reades;

import android.content.Context;

import com.orm.SugarApp;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import ua.org.cofriends.reades.utils.LocalStorage;

public class MainApplication extends SugarApp {

    private ObjectGraph mApplicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        LocalStorage.INSTANCE.init(this);
        mApplicationGraph = ObjectGraph.create(getModules().toArray());
    }

    /**
     * Can be inherited to provide additional modules. Just call super implementation and add to it.
     * @return list of modules
     */
    protected List<Object> getModules() {
        return Arrays.asList(Modules.list(this));
    }

    public <T> T inject(T object) {
        return mApplicationGraph.inject(object);
    }

    public ObjectGraph objectGraph() {
        return mApplicationGraph;
    }

    public static MainApplication get(Context context) {
        return (MainApplication) context.getApplicationContext();
    }
}
