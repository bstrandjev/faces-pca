package com.borisp.faces.classification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;


/**
 * A class that loads application for the training classification with all its data.
 *
 * @author Boris
 */
public class ClassificationApplicationPreparator {
    /** The path of the images that are to be transported to the classification app. */
    private static final String PATH_TO_MANIPULATED = "images/manipulated";
    // Classification app constants
    /** The path in which to store the images to be classified. **/
    public static final String CLASSIFICATION_APP_IMAGE_PATH =
            "../MasterThesisAndroid/res/drawable/img%d.jpg";
    /** The path to the input json for the classification app. */
    public static final String CLASSIFICATION_APP_JSON_PATH =
            "../MasterThesisAndroid/assets/faces.json";

    public static void prepareClassificationApplication() throws IOException {
        File manipulatedDir = new File(PATH_TO_MANIPULATED);
        List<Face> faces = new ArrayList<Face>();
        int index = 0;
        for (File manipulatedFile : manipulatedDir.listFiles()) {
            // skipping the svn files
            if (manipulatedFile.getName().startsWith(".")) {
                continue;
            }

            Face face = new Face();
            String name = manipulatedFile.getName();
            face.setKey(name.substring(0, name.lastIndexOf(".")));
            face.setIndex(index++);
            faces.add(face);

            ColorPixel[][] imagePixels = ImageReader.getImagePixels(manipulatedFile);
            int[][] imageGrayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            for (int i = 0; i < imagePixels.length; i++) {
                for (int j = 0; j < imagePixels[0].length; j++) {
                    imagePixels[i][j] = GrayscaleConverter.getColorPixel(imageGrayscale[i][j]);
                }
            }
            String classificationAppImagePath = String.format(CLASSIFICATION_APP_IMAGE_PATH, index);
            ImageWriter.createImage(new File(classificationAppImagePath), imagePixels, false);

        }
        JsonParser parser = new JsonParser();
        String serializeFaces = parser.serializeFaces(faces);

        FileOutputStream jsonOutputStream = new FileOutputStream(CLASSIFICATION_APP_JSON_PATH);
        jsonOutputStream.write(serializeFaces.getBytes());
        jsonOutputStream.flush();
        jsonOutputStream.close();
    }
}
