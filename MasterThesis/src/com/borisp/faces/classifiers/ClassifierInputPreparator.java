package com.borisp.faces.classifiers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.database.DatabaseHelper;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;

public class ClassifierInputPreparator {

    /**
     * Creates a list of {@link Example}s for all classifications of a user for transformation.
     *
     * @param username The username of the user that did the classification to use as input
     * @param transformationId The if of the transformation to use to transform the input
     * @param countedEigenFaces The number of dimensions which to include in the analysis
     * @param sessionFactory A session factory to use for the database connections.
     */
    public static List<Example> generateClassifierPcaInput(String username, int transformationId,
            int countedEigenFaces, SessionFactory sessionFactory) {
        User user = DatabaseHelper.getUserByUsername(username, sessionFactory);
        Transformation transformation =
                DatabaseHelper.getTransformationById(transformationId, sessionFactory);
        Manipulation manipulation = transformation.getManipulation();
        List<Classification> classifications =
                DatabaseHelper.getNeededClassifications(user, manipulation, sessionFactory);

        List<Example> examples = new ArrayList<Example>();
        for (Classification classification : classifications) {
            List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                    classification.getManipulatedImage(), transformation, sessionFactory);
            Example example = new Example();
            example.measures = new double[countedEigenFaces];
            for (int i = 0; i < countedEigenFaces; i++) {
                example.measures[i] = pcaCoeficients.get(i).getCoeficient();
            }
            example.classification = (classification.getIsBeautiful() == 1) ? 0 : 1;
            examples.add(example);
        }
        return examples;
    }

    /**
     * Creates a list of {@link Example}s for all classifications of manipulation images
     *
     * @param username The username of the user that did the classification to use as input
     * @param transformationId The if of the transformation to use to transform the input
     * @param sessionFactory A session factory to use for the database connections.
     */
    public static List<Example> generateClassifierManipulationInput(String username,
            int transformationId, SessionFactory sessionFactory) {
        User user = DatabaseHelper.getUserByUsername(username, sessionFactory);
        Transformation transformation =
                DatabaseHelper.getTransformationById(transformationId, sessionFactory);
        Manipulation manipulation = transformation.getManipulation();
        List<Classification> classifications =
                DatabaseHelper.getNeededClassifications(user, manipulation, sessionFactory);

        List<Example> examples = new ArrayList<Example>();
        for (Classification classification : classifications) {
            String manipulatedImagePath = classification.getManipulatedImage()
                    .getManipulatedImagePath();
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(new File(manipulatedImagePath));
            int[][] grayscales = GrayscaleConverter.getImageGrayscale(imagePixels);
            Example example = new Example();
            example.measures = new double[grayscales.length * grayscales[0].length];
            int idx = 0;
            for (int i = 0; i < grayscales.length; i++) {
                for (int j = 0; j < grayscales[0].length; j++) {
                    example.measures[idx++] = grayscales[i][j];
                }
            }
            example.classification = (classification.getIsBeautiful() == 1) ? 0 : 1;
            examples.add(example);
        }
        return examples;
    }
}
