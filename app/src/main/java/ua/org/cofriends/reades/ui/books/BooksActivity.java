package ua.org.cofriends.reades.ui.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileScopeType;
import com.devpaul.filepickerlibrary.enums.FileType;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.ui.basic.ListAddActivity;
import ua.org.cofriends.reades.ui.read.ReadActivity;
import ua.org.cofriends.reades.utils.BundleUtils;

public class BooksActivity extends ListAddActivity {

    @InjectView(R.id.pager)
    ViewPager pager;

    public static void start(Dictionary dictionary, Context context) {
        Bundle extras = BundleUtils.writeObject(Dictionary.class, dictionary);
        context.startActivity(new Intent(context, BooksActivity.class).putExtras(extras));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
        SavedDictionariesService.setCurrentDictionary(dictionary);

        ButterKnife.inject(this);

        pager.setAdapter(new BookPagerAdapter(this));
    }

    @Override
    protected int getDownloadViewId() {
        return R.layout.download_books_view;
    }

    @Override
    protected int getSavedViewId() {
        return R.layout.saved_books_activity;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onBookSelected(Book.SelectedEvent event) {
        Dictionary dictionary = BundleUtils.fetchFromBundle(Dictionary.class, getIntent().getExtras());
        ReadActivity.start(event.getData(), dictionary, this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void oтBookOpen(DeviceBooksView.OpenBookEvent event) {
        Intent filePicker = new Intent(this, FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, FileScopeType.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, R.color.light_indigo);
        filePicker.putExtra(FilePickerActivity.MIME_TYPE, FileType.PDF);
        startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerActivity.REQUEST_DIRECTORY && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if (filePath != null) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}