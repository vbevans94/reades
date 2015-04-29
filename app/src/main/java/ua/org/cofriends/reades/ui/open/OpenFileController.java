package ua.org.cofriends.reades.ui.open;

import android.content.Context;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.service.book.SavedBooksService;
import ua.org.cofriends.reades.service.dictionary.SavedDictionariesService;
import ua.org.cofriends.reades.utils.BookAdapterFactory;

@Singleton
public class OpenFileController {

    @Inject
    BookAdapterFactory bookAdapterFactory;

    public void showOpenDialog(final Context context) {
        new OpenFileDialog(context)
                .setFilter(bookAdapterFactory.fileNamePattern())
                .setAccessDeniedMessage(context.getString(R.string.message_access_denied))
                .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                    @Override
                    public void OnSelectedFile(String filePath) {
                        File file = new File(filePath);

                        Book book = new Book();
                        book.setName(file.getName()); // when book opened from device, file name is book name
                        book.setSourceType(Book.SourceType.DEVICE);
                        book.setLoadedPath(filePath);
                        book.setLanguage(SavedDictionariesService.getCurrent().getFromLanguage());

                        SavedBooksService.actUpon(context, book, SavedBooksService.SAVE);
                    }
                })
                .show();
    }
}
