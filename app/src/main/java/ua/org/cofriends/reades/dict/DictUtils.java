package ua.org.cofriends.reades.dict;

import org.dict.server.DatabaseFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DictUtils {

    private static final int CAPACITY = 8;
    private static DictThread sWorker;
    private static BlockingQueue<String> sQueue;

    public static void start(String configPath) {
        sQueue = new ArrayBlockingQueue<String>(CAPACITY);
        sWorker = new DictThread(sQueue, DatabaseFactory.getEngine(configPath));
        sWorker.start();
    }

    public static void search(CharSequence word) {
        sQueue.offer(word.toString());
    }

    public static void stop() {
        if (sWorker != null) {
            sWorker.cancel();
            sWorker = null;
        }
        sQueue = null;
    }
}
