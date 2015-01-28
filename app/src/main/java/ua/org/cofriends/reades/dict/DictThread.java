package ua.org.cofriends.reades.dict;

import org.dict.kernel.Answer;
import org.dict.kernel.IAnswer;
import org.dict.kernel.IDictEngine;
import org.dict.kernel.IRequest;
import org.dict.kernel.SimpleRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.BlockingQueue;

import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.Logger;

class DictThread extends Thread {

    private final static String TAG = Logger.makeLogTag(DictThread.class);
    private final BlockingQueue<String> mWordQueue;
    private final IDictEngine mDictEngine;
    private boolean mCancelled;

    DictThread(BlockingQueue<String> queue, IDictEngine engine) {
        mWordQueue = queue;
        mDictEngine = engine;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String word = mWordQueue.take();

                IAnswer[] answers = request(word);
                BusUtils.post(new DictService.AnswerEvent(answers));
            } catch (InterruptedException | UnsupportedEncodingException e) {
                Logger.e(TAG, "Interrupted when getting word", e);
            }
            if (mCancelled) {
                break;
            }
        }
    }

    private IAnswer[] request(String word) throws UnsupportedEncodingException {
        IRequest request = new SimpleRequest("", "db=*&word=" + URLEncoder.encode(word, "utf-8"));
        return mDictEngine.lookup(request);
    }

    void cancel() {
        mCancelled = true;
    }
}
