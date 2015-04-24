package ua.org.cofriends.reades.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.utils.RequestCodes;

@Singleton
public class OpenFileController {

    @Inject
    BookAdapterFactory bookAdapterFactory;

    public void showOpenDialog(Activity activity) {
        Intent filePicker = new Intent(activity, FileChooserActivity.class);
        bookAdapterFactory.addFormats(filePicker);
        Intent getContentIntent = FileUtils.createGetContentIntent();

        Intent intent = Intent.createChooser(getContentIntent, activity.getString(R.string.title_file_picker));
        activity.startActivityForResult(intent, RequestCodes.FILE_PICKER);
    }

    public void processResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.FILE_PICKER && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = FileUtils.getFile(context, uri);
            if (file != null) {
                String filePath = file.getAbsolutePath();

                Book book = new Book();
                book.setName(file.getName()); // when book opened from device, file name is book name
                book.setSourceType(Book.SourceType.DEVICE);
                book.setLoadedPath(filePath);
                book.setLanguage(SavedDictionariesService.getCurrent().getFromLanguage());

                SavedBooksService.actUpon(context, book, SavedBooksService.SAVE);
            }
        }
    }
}
