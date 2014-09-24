package ua.org.cofriends.reades.utils;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

    private static final String TAG = Logger.makeLogTag(FileUtils.class);

    /**
     * @return text contained in the file or null in case of some error while reading file
     */
    public static String readText(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                builder.append(line);
                builder.append(lineSeparator());
                line = reader.readLine();
            }
            return builder.toString();
        } catch (IOException e) {
            Logger.e(TAG, "Error on reading text from file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, "Failed to close stream", e);
            }
        }
        return null;
    }

    /**
     * @return line separator
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String lineSeparator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ? System.lineSeparator()
                : "\n";
    }
}
