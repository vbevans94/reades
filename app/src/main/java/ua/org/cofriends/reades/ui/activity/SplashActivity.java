package ua.org.cofriends.reades.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.DownloadService;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.utils.LocalStorage;

public class SplashActivity extends ActionBarActivity {

    private Dictionary mLoadedDictionary;
    private Book mLoadedBook;

    private StartupMode mMode = StartupMode.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);

        int bookId = LocalStorage.INSTANCE.getInt(getString(R.string.key_book_id));
        int dictionaryId = LocalStorage.INSTANCE.getInt(getString(R.string.key_dictionary_id));
        if (bookId != 0) {
            mMode = StartupMode.CONTINUE_READING;
            SavedDictionariesService.loadById(dictionaryId, SplashActivity.this);
            SavedBooksService.loadById(bookId, SplashActivity.this);
        }

		// Start timer and launch main activity
		IntentLauncher launcher = new IntentLauncher();
		launcher.start();
	}

    @SuppressWarnings("unused")
    public void onEventMainThread(Dictionary.LoadedEvent event) {
        mLoadedDictionary = event.getData();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Book.LoadedEvent event) {
        mLoadedBook = event.getData();
    }

    private void startUp() {
        if (mLoadedBook != null && mLoadedDictionary != null) {
            ReadActivity.start(mLoadedBook, mLoadedDictionary, this);
        } else {
            startActivity(new Intent(this, DictionariesActivity.class));
        }
        finish();
    }

	private class IntentLauncher extends Thread {
		@Override
		/**
		 * Sleep for some time and than start new activity.
		 */
		public void run() {
			try {
				// Sleeping
				Thread.sleep(mMode.mTimeOut);
			} catch (InterruptedException e) {
				// Error
			}

			// Start main activity
            startUp();
		}
	}

    private enum StartupMode {

        HOME(700l), CONTINUE_READING(1000l);

        private final long mTimeOut;

        private StartupMode(long timeOut) {
            mTimeOut = timeOut;
        }
    }
}