package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.List;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.EventBusUtils;
import ua.org.cofriends.reades.utils.Utils;

public class LocalDictionariesService extends IntentService {

    public LocalDictionariesService() {
        super(LocalDictionariesService.class.getSimpleName());
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, LocalDictionariesService.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Iterator<Dictionary> dictionaryIterator = Dictionary.findAll(Dictionary.class);
        EventBusUtils.getBus().post(new DictionariesLoadedEvent(Utils.toArrayList(dictionaryIterator)));
    }

    public static class DictionariesLoadedEvent extends EventBusUtils.Event<List<Dictionary>> {

        public DictionariesLoadedEvent(List<Dictionary> dictionaries) {
            super(dictionaries);
        }
    }
}
