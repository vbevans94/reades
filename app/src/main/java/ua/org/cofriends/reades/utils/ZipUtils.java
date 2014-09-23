package ua.org.cofriends.reades.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    private static final String TAG = Logger.makeLogTag(ZipUtils.class);

    /**
     * Extracts zip archive into directory.
     * @param zipFile to extract
     * @param destination directory where to extract
     * @return unzipped directory absolute path
     */
    public static String unzip(String zipFile, String destination) {
        String zipRootDirectory = null;
        try {
            checkDirectory(destination);
            FileInputStream inputStream = new FileInputStream(zipFile);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Logger.v(TAG, "Unzipping " + zipEntry.getName());

                String absolutePath = destination + zipEntry.getName();
                if (zipRootDirectory == null) {
                    zipRootDirectory = absolutePath;
                }
                if (zipEntry.isDirectory()) {
                    checkDirectory(absolutePath);
                } else {
                    FileOutputStream outputStream = new FileOutputStream(absolutePath);
                    byte[] buffer = new byte[1024]; // kilobyte
                    while (true) {
                        int readBytes = zipInputStream.read(buffer);
                        if (readBytes == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, readBytes);
                    }

                    zipInputStream.closeEntry();
                    outputStream.close();
                }

            }
            zipInputStream.close();
        } catch (IOException e) {
            Logger. e(TAG, "Error on unzip", e);
        }

        return zipRootDirectory;
    }

    private static void checkDirectory(String directory) {
        File f = new File(directory);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}