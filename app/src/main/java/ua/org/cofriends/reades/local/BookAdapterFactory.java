package ua.org.cofriends.reades.local;

import android.content.Context;
import android.content.Intent;

import com.devpaul.filepickerlibrary.FilePickerActivity;
import com.devpaul.filepickerlibrary.enums.FileType;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.entity.Book;

@Singleton
public class BookAdapterFactory {

    private final static Map<Book.FormatType, BookAdapter> TRANSFORMER_MAP = new HashMap<>();

    static {
        TRANSFORMER_MAP.put(Book.FormatType.TXT, new TextTransformer());
    }

    @Inject
    BookAdapterFactory() {
    }

    public Book.FormatType formatType(String path) {
        if (path.endsWith("pdf")) {
            return Book.FormatType.PDF;
        } else {
            return Book.FormatType.TXT;
        }
    }

    public BookAdapter adapterFor(Book.FormatType formatType) {
        return TRANSFORMER_MAP.get(formatType);
    }

    public void addFormats(Intent intent) {
        // we still don't support pdfs
        // filePicker.putExtra(FilePickerActivity.MIME_TYPE, FileType.PDF);
        intent.putExtra(FilePickerActivity.MIME_TYPE, FileType.TXT);
    }

    public interface BookAdapter {

        /**
         * Adapts book retrieved from any source to be used by app on the device.
         *
         * @param book to be adapted, contains all needed information
         */
        void adapt(Context context, Book book);
    }

    private static class TextTransformer implements BookAdapter {

        @Override
        public void adapt(Context context, Book book) {
            book.setFormatType(Book.FormatType.TXT);
            if (book.getSourceType() == Book.SourceType.DEVICE) {
                // if book is opened on device by user we should just copy it into our local storage
                File oldFile = new File(book.getFileUrl());
                File file = new File(getStoragePath(context) + oldFile.getName());
                // in this case book name can only be derived from file name
                book.setName(file.getName());
                try {
                    Files.copy(oldFile, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // if book was downloaded from the server, it just need to be moved from temp file
                File file = new File(book.getFileUrl());
                file.renameTo(new File(getStoragePath(context) + file.getName()));
            }
        }

        private String getStoragePath(Context context) {
            return context.getFilesDir().getAbsolutePath() + "/";
        }
    }
}
