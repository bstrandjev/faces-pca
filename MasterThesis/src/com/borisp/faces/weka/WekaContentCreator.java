package com.borisp.faces.weka;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.database.DatabaseHelper;

/**
 * A class that can be used to generate a file that can be feeded into the weka system.
 *
 * @author Boris
 */
public class WekaContentCreator {
    private static final String WEKA_INPUT_FILE_PATTERN =
            "users\\%s\\manipulation_%02d\\weka_input.arff";
    private static final String RELATION_STRING = "@relation faces_pca";
    private static final String ATTRIBUTE_PATTERN = "@attribute eigen%03d numeric\n";
    private static final String TARGET_ATTRIBUTE_STRING = "@attribute beautiful {true, false}";
    private static final String DATA_STRING = "@data";

    /**
     * Generates file that can be used as input to the weka system
     * <p>
     * This method works only for sets associated with single manipulation.
     *
     * @param username The username of the user that did the classification to use as input
     * @param manipulationIndex The index of the manipulation which to be used as input for weka
     * @param sessionFactory A session factory to use for the database connections.
     * @throws IOException
     */
    public static void generateWekaInput(String username, int transformationId,
            int manipulationIndex, SessionFactory sessionFactory) throws IOException {
        User user = DatabaseHelper.getUserByUsername(username, sessionFactory);
        Transformation transformation =
                DatabaseHelper.getTransformationById(transformationId, sessionFactory);
        List<ManipulatedImage> manipulatedImages = transformation.getAllManipulatedImages();
        List<Classification> classifications =
                DatabaseHelper.getNeededClassifications(user, manipulatedImages, sessionFactory);

        String wekaInputFilePath = String.format(WEKA_INPUT_FILE_PATTERN, username,
                manipulationIndex);
        FileOutputStream wekaFile = new FileOutputStream(new File(wekaInputFilePath));
        BufferedOutputStream bufferedWekaStream = new BufferedOutputStream(wekaFile);

        bufferedWekaStream.write(RELATION_STRING.getBytes());
        bufferedWekaStream.write("\n\n".getBytes());
        for (int i = 0; i < transformation.getEigenFaces().size(); i++) {
            bufferedWekaStream.write(String.format(ATTRIBUTE_PATTERN, i).getBytes());
        }
        bufferedWekaStream.write(TARGET_ATTRIBUTE_STRING.getBytes());
        bufferedWekaStream.write("\n\n".getBytes());

        bufferedWekaStream.write(DATA_STRING.getBytes());
        bufferedWekaStream.write("\n\n".getBytes());

        for (Classification classification : classifications) {
            List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                    classification.getManipulatedImage(), transformation, sessionFactory);
            StringBuffer sb = new StringBuffer();
            for (PcaCoeficient pcaCoeficient : pcaCoeficients) {
                sb.append(String.format("%.6f,", pcaCoeficient.getCoeficient()));
            }
            sb.append(classification.getIsBeautiful() == 0 ? "false\n" : "true\n");
            bufferedWekaStream.write(sb.toString().getBytes());
        }
        bufferedWekaStream.close();
    }
}
