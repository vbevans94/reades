package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class LocalDictionariesService extends IntentService {

    public LocalDictionariesService() {
        super(LocalDictionariesService.class.getSimpleName());
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, LocalDictionariesService.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Dictionary> dictionaries = Dictionary.listAll(Dictionary.class);
        EventBusUtils.getBus().post(new Dictionary.ListLoadedEvent(dictionaries));
    }
}
