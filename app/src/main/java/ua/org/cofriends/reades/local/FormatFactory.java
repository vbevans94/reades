package ua.org.cofriends.reades.local;

import android.app.Activity;
import android.content.Context;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ua.org.cofriends.reades.entity.Book;

@Singleton
public class FormatFactory {

    private final static Map<Book.FormatType, Transformer> TRANSFORMER_MAP = new HashMap<>();

    static {
        TRANSFORMER_MAP.put(Book.FormatType.TXT, new TextTransformer());
    }

    @Inject
    Activity activity;

    public Book.FormatType formatType(String path) {
        if (path.endsWith("pdf")) {
            return Book.FormatType.PDF;
        } else {
            return Book.FormatType.TXT;
        }
    }

    public Transformer transformer(Book.FormatType formatType) {
        return TRANSFORMER_MAP.get(formatType);
    }

    public interface Transformer {

        /**
         * Transforms source at the given path to a text file and return its new path.
         *
         * @param oldPath of the file to transform
         * @return new path of text file
         */
        String transform(Context context, String oldPath);
    }

    private static class TextTransformer implements Transformer {

        @Override
        public String transform(Context context, String oldPath) {
            File oldFile = new File(oldPath);
            // just copy from the source to the destination
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + oldFile.getName());
            try {
                Files.copy(oldFile, file);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return file.getAbsolutePath();
        }
    }
}
