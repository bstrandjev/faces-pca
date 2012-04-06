package com.borisp.faces.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * Utility methods for parsing streams.
 *
 * @author boris.strandjev
 */
public class IOHelper {
    public static final String LOG_TAG = "IOHelper";

    /**
     * Returns the content of the given asset file.
     *
     * @param assets The assets from which to fetch the file.
     * @param fileName The name of the file for which to fetch the content.
     * @return The content or null if an error occurred while accessing.
     */
    public static String getFileContent(AssetManager assets, String fileName) {
        String content = null;
        try {
            InputStream inputStream = assets.open(fileName);
            content = parseStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG,  "Error while reading file " + fileName, e);
        }
        return content;
    }

    /**
     * Parses an InputStream to a String.
     *
     * @param inputStream The stream to be parsed.
     * @return The stream content as string.
     */
    public static String parseStream(InputStream inputStream) {
        String content = null;
        try {
            int BUFFER_SIZE = 1024;
            byte [] buffer = new byte[BUFFER_SIZE];
            StringBuilder builder = new StringBuilder();
            int count;
            while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                builder.append(new String(buffer, 0, count));
            }
            content = builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while reading input stream", e);
        }
        return content;
    }

    /**
     * Copies the given file to the specified location.
     *
     * @param from A input stream for the contents of the stream to copy.
     * @param toLocation The location in which to copy the stream.
     */
    public static void copyFile(InputStream from, String toLocation) {
        try {
            File remoteFile = new File(toLocation);
            remoteFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(new File(toLocation));
            out.write(parseStream(from).getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while copying file", e);
        }
    }
}
