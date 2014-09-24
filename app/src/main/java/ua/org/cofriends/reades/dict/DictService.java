package ua.org.cofriends.reades.dict;

import org.dict.kernel.IAnswer;
import org.dict.server.DatabaseFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ua.org.cofriends.reades.utils.EventBusUtils;

public class DictService {

    private static final int CAPACITY = 8;
    private static final Map<String, DictService> RUNNING_SERVICES = new HashMap<String, DictService>();

    private DictThread mWorker;
    private BlockingQueue<String> mQueue;

    private DictService(String configPath) {
        mQueue = new ArrayBlockingQueue<String>(CAPACITY);
        mWorker = new DictThread(mQueue, DatabaseFactory.getEngine(configPath));
        mWorker.start();
    }

    /**
     * Creates or takes from cache a dict service for given dict config file path.
     * @param configPath to get dict service for
     * @return instance of {@link ua.org.cofriends.reades.dict.DictService}
     */
    public static DictService getStartedService(String configPath) {
        if (!RUNNING_SERVICES.containsKey(configPath)) {
            RUNNING_SERVICES.put(configPath, new DictService(configPath));
        }
        return RUNNING_SERVICES.get(configPath);
    }

    /**
     * Searches for the word in the dict.
     * For response listen to {@link ua.org.cofriends.reades.dict.DictService.AnswerEvent}.
     * @param word to search for
     */
    public void search(CharSequence word) {
        mQueue.offer(word.toString());
    }

    private void stop() {
        if (mWorker != null) {
            mWorker.cancel();
            mWorker = null;
        }
        mQueue = null;
    }

    /**
     * Finds and if found stops service by config file path.
     * @param configPath to search service for
     */
    public static void stopByPath(String configPath) {
        DictService service = RUNNING_SERVICES.get(configPath);
        if (service != null) {
            service.stop();
            RUNNING_SERVICES.remove(configPath);
        }
    }

    public static class AnswerEvent extends EventBusUtils.Event<IAnswer[]> {

        public AnswerEvent(IAnswer[] object) {
            super(object);
        }
    }
}
