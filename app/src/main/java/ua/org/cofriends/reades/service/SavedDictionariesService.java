package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SavedDictionariesService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final String EXTRA_TYPE = "extra_type";

    public SavedDictionariesService() {
        super(SavedDictionariesService.class.getSimpleName());
    }

    public static void loadList(Context context) {
        context.startService(new Intent(context, SavedDictionariesService.class)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    public static void save(Context context, Dictionary dictionary) {
        context.startService(new Intent(context, SavedDictionariesService.class)
                .putExtra(EXTRA_TYPE, SAVE)
                .putExtras(BundleUtils.writeObject(Dictionary.class, dictionary)));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE:
                Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, intent.getExtras());
                dictionary.save();
            case LOAD_LIST:
                List<Dictionary> dictionaries = Dictionary.listAll(Dictionary.class);
                EventBusUtils.getBus().post(new Dictionary.ListLoadedEvent(dictionaries));
                break;
        }
    }
}
