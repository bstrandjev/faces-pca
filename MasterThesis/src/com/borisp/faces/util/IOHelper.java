package com.borisp.faces.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class defining helper methods for input / output.
 *
 * @author Boris
 */
public class IOHelper {

    /**
     * Returns the string content of the given file.
     *
     * @param filePath The path to the file for which to fetch the content.
     * @return The content or null if an error occurred while accessing.
     */
    public static String getFileContent(String filePath) {
        String content = null;
        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
            content = parseStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Parses an InputStream to a String.
     *
     * @param inputStream The stream to be parsed.
     * @return The stream content as string.
     * @throws IOException If error occurs while reading the file.
     */
    private static String parseStream(InputStream inputStream) throws IOException {
        String content = null;
        int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder builder = new StringBuilder();
        int count;
        while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            builder.append(new String(buffer, 0, count));
        }
        content = builder.toString();
        return content;
    }

}
