package ua.org.cofriends.reades.local;

import android.content.Context;
import android.content.Intent;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.TextWord;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.entity.Book;
import ua.org.cofriends.reades.utils.IntentExtras;

@Singleton
public class BookAdapterFactory {

    private final static Map<Book.FormatType, BookFileAdapter> TRANSFORMER_MAP = new HashMap<>();

    static {
        TRANSFORMER_MAP.put(Book.FormatType.TXT, new TextFormatAdapter());
        TRANSFORMER_MAP.put(Book.FormatType.PDF, new PdfFormatAdapter());
    }

    @Inject
    BookAdapterFactory() {
    }

    /**
     * Founds out format type for a given book.
     *
     * @param book to find format type for
     * @return format type
     */
    private Book.FormatType formatType(Book book) {
        if (book.getFileUrl().endsWith("pdf")) {
            return Book.FormatType.PDF;
        } else {
            return Book.FormatType.TXT;
        }
    }

    public BookFileAdapter adapterFor(Book book) {
        // if we have book without format type set, we find it
        if (book.getFormatType() == null) {
            book.setFormatType(formatType(book));
        }
        // return adapter for format type
        return TRANSFORMER_MAP.get(book.getFormatType());
    }

    public void addFormats(Intent intent) {
        intent.putExtra(IntentExtras.FILE_PATTERN, "*.pdf|*.txt");
    }

    public interface BookFileAdapter {

        /**
         * Adapts book retrieved from any source to be used by app on the device.
         *
         * @param book to be adapted, contains all needed information
         */
        void adapt(Context context, Book book);
    }

    private static class TextFormatAdapter implements BookFileAdapter {

        @Override
        public void adapt(Context context, Book book) {
            if (book.getSourceType() == Book.SourceType.DEVICE) {
                // if book is opened on device by user we should just copy it into our local storage
                copy(context, book);
            } else {
                // if book was downloaded from the server, it just need to be moved from temp file
                move(context, book);
            }
        }
    }

    private static class PdfFormatAdapter implements BookFileAdapter {

        @Override
        public void adapt(Context context, Book book) {
            try {
                File bookFile = new File(book.getFileUrl());

                // read pdf content as text to string
                MuPDFCore core = new MuPDFCore(context, book.getFileUrl());
                StringBuilder builder = new StringBuilder();

                int count = core.countPages();
                for (int i = 0; i < count; i++) {
                    TextWord[][] lines = core.textLines(i);

                    for (TextWord[] line : lines) {
                        for (TextWord word : line) {
                            builder.append(word.w).append(' ');
                        }
                        builder.append(System.lineSeparator());
                    }
                }

                if (book.getSourceType() == Book.SourceType.LIBRARY) {
                    // we need to remove temp pdf file loaded from the net
                    bookFile.delete();
                }

                // write content to file
                File destinationFile = getDestinationFile(context, bookFile);
                Files.write(builder.toString(), destinationFile, Charset.forName("UTF-8"));
                book.setLoadedPath(destinationFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copies book file from current {@link Book#getFileUrl()} to destination file and resets book's file url value.
     *
     * @param context to use
     * @param book    to copy file of
     */
    private static void copy(Context context, Book book) {
        File bookFile = new File(book.getFileUrl());
        File destinationFile = getDestinationFile(context, bookFile);
        // in this case book name can only be derived from file name
        try {
            Files.copy(bookFile, destinationFile);

            book.setLoadedPath(destinationFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves book file from current {@link Book#getFileUrl()} to destination file and resets book's file url value.
     *
     * @param context to use
     * @param book    to move file of
     */
    private static void move(Context context, Book book) {
        File bookFile = new File(book.getFileUrl());
        bookFile.renameTo(getDestinationFile(context, bookFile));

        book.setLoadedPath(bookFile.getAbsolutePath());
    }

    private static File getDestinationFile(Context context, File currentFile) {
        return new File(context.getFilesDir().getAbsolutePath() + "/" + currentFile.getName());
    }
}
