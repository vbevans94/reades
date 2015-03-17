package ua.org.cofriends.reades.local;

import android.app.Activity;
import android.content.Intent;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileScopeType;
import com.devpaul.filepickerlibrary.enums.FileType;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;

@Singleton
public class OpenFileController {

    @Inject
    Activity activity;

    @Inject
    FormatFactory formatFactory;

    public void showOpenDialog() {
        Intent filePicker = new Intent(activity, FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE_TYPE, FileScopeType.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST_CODE, FilePickerActivity.REQUEST_FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, R.color.light_indigo);
        filePicker.putExtra(FilePickerActivity.MIME_TYPE, FileType.PDF);
        filePicker.putExtra(FilePickerActivity.MIME_TYPE, FileType.TXT);
        activity.startActivityForResult(filePicker, FilePickerActivity.REQUEST_FILE);
    }

    public void processResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerActivity.REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            if (filePath != null) {
                Book book = new Book();
                book.setFormatType(formatFactory.formatType(filePath));
                book.setSourceType(Book.SourceType.DEVICE);

                SavedBooksService.actUpon(activity, book, SavedBooksService.SAVE);
            }
        }
    }
}
