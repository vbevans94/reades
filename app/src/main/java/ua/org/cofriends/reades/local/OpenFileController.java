package ua.org.cofriends.reades.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileScopeType;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;

@Singleton
public class OpenFileController {

    @Inject
    BookAdapterFactory bookAdapterFactory;

    public void showOpenDialog(Activity activity) {
        Intent filePicker = new Intent(activity, FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, FileScopeType.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, R.color.light_indigo);
        bookAdapterFactory.addFormats(filePicker);
        activity.startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
    }

    public void processResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerActivity.REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if (filePath != null) {
                Book book = new Book();
                book.setFormatType(bookAdapterFactory.formatType(filePath));
                book.setSourceType(Book.SourceType.DEVICE);
                book.setLoadedPath(filePath);
                book.setLanguage(SavedDictionariesService.getCurrent().getFromLanguage());

                SavedBooksService.actUpon(context, book, SavedBooksService.SAVE);
            }
        }
    }
}
