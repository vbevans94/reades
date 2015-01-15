package ua.org.cofriends.reades.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.Collections;
import java.util.List;

import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

public class SavedWordsService extends IntentService {

    private static final int LOAD_LIST = 0;
    private static final int SAVE = 1;
    private static final int DELETE = 2;
    private static final String EXTRA_TYPE = "extra_type";

    public SavedWordsService() {
        super(SavedWordsService.class.getSimpleName());
    }

    public static void loadList(Context context) {
        context.startService(new Intent(context, SavedWordsService.class)
                .putExtra(EXTRA_TYPE, LOAD_LIST));
    }

    public static void save(Context context, Word word) {
        context.startService(new Intent(context, SavedWordsService.class)
                .putExtra(EXTRA_TYPE, SAVE)
                .putExtras(BundleUtils.writeObject(Word.class, word)));
    }

    public static void delete(Context context, Word word) {
        context.startService(new Intent(context, SavedWordsService.class)
                .putExtra(EXTRA_TYPE, DELETE)
                .putExtras(BundleUtils.writeObject(Word.class, word)));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(EXTRA_TYPE, LOAD_LIST);

        switch (type) {
            case SAVE: {
                Word word = BundleUtils.fetchFromBundle(Word.class, intent.getExtras());

                List<Word> saved = Word.find(Word.class, "WORD = ?", word.getWord());
                // don't save the same word twice
                if (saved.isEmpty()) {
                    word.save();
                } else {
                    // reorder so that newly saved word was on top
                    saved.get(0).delete();
                    word.save();
                }

                loadAll();
                break;
            }

            case DELETE: {
                Word word = BundleUtils.fetchFromBundle(Word.class, intent.getExtras());
                List<Word> saved = Word.find(Word.class, "WORD = ?", word.getWord());
                // don't save the same word twice
                if (!saved.isEmpty()) {
                    saved.get(0).delete();
                }
                loadAll();
                break;
            }

            case LOAD_LIST:
                loadAll();
                break;
        }
    }

    private void loadAll() {
        // retrieve updated list of all word from the database and give to the world
        List<Word> words = Word.listAll(Word.class);
        Collections.reverse(words);
        BusUtils.post(new Word.ListLoadedEvent(words));
    }
}
