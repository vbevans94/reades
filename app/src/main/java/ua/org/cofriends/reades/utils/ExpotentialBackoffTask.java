package ua.org.cofriends.reades.utils;

import android.os.AsyncTask;

import java.util.Random;

public class ExpotentialBackoffTask extends AsyncTask<ExpotentialBackoffTask.Work, Void, Void> {

    private static final String TAG = Logger.makeLogTag(ExpotentialBackoffTask.class);
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;

    @Override
    protected Void doInBackground(Work... params) {
        long backoff = BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Logger.v(TAG, "Attempt #" + i + " to run");
            try {
                for (Work work : params) {
                    work.doWork();
                }

                break;
            } catch (Exception e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Logger.d(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Logger.v(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Logger.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        return null;
    }

    public interface Work {

        public void doWork() throws Exception;

    }

}
