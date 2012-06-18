package com.borisp.faces.classifiers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.classifiers.examples.ComplexExample;
import com.borisp.faces.classifiers.examples.Example;
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
            Example example = new Example();
            example.measures = getPcaMeasures(countedEigenFaces, classification, transformation,
                    sessionFactory);
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
            Example example = new Example();
            example.measures = getInitialMeasures(classification);
            example.classification = (classification.getIsBeautiful() == 1) ? 0 : 1;
            examples.add(example);
        }
        return examples;
    }

    /**
     * Creates a list of {@link ComplexExample}s for all classifications of a user for
     * transformation.
     *
     * @param username The username of the user that did the classification to use as input
     * @param transformationId The if of the transformation to use to transform the input
     * @param countedEigenFaces The number of dimensions which to include in the analysis
     * @param sessionFactory A session factory to use for the database connections.
     */
    public static List<ComplexExample> generateClassifierComplexInput(String username,
            int transformationId, int countedEigenFaces, SessionFactory sessionFactory) {
        User user = DatabaseHelper.getUserByUsername(username, sessionFactory);
        Transformation transformation = DatabaseHelper.getTransformationById(transformationId,
                sessionFactory);
        Manipulation manipulation = transformation.getManipulation();
        List<Classification> classifications = DatabaseHelper.getNeededClassifications(user,
                manipulation, sessionFactory);

        List<ComplexExample> examples = new ArrayList<ComplexExample>();
        for (Classification classification : classifications) {
            ComplexExample example = new ComplexExample();
            example.pcaMeasures = getPcaMeasures(countedEigenFaces, classification, transformation,
                    sessionFactory);
            example.initialMeasures = getInitialMeasures(classification);
            example.classification = (classification.getIsBeautiful() == 1) ? 0 : 1;
            examples.add(example);
        }
        return examples;
    }

    /**
     * Creates an array of coeficients in the PCA space for a given image.
     *
     * This array will be used as input to different classifiers.
     * @param countedEigenFaces The number of eigen faces to consider
     * @param classification The classification from which to fetch the image.
     * @param transformation The transformation defining the pca coeficients
     * @param sessionFactory The session factory to use.
     */
    private static double[] getPcaMeasures(int countedEigenFaces, Classification classification,
            Transformation transformation, SessionFactory sessionFactory) {
        List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                classification.getManipulatedImage(), transformation, sessionFactory);
        double[] measures = new double[countedEigenFaces];
        for (int i = 0; i < countedEigenFaces; i++) {
            measures[i] = pcaCoeficients.get(i).getCoeficient();
        }
        return measures;
    }

    /**
     * Creates an array of the pixel of the non-transformed image.
     *
     * This array will be used as input to different classifiers.
     * @param classification The classification from which to fetch the image.
     */
    private static double [] getInitialMeasures(Classification classification) {
        return getInitialMeasures(classification.getManipulatedImage());
    }

    /**
     * Creates an array of the pixel of the non-transformed image.
     *
     * This array will be used as input to different classifiers.
     * @param manipulatedImage The manipulated image for which to fetch the measures.
     */
    public static double [] getInitialMeasures(ManipulatedImage manipulatedImage) {
        String manipulatedImagePath = manipulatedImage.getManipulatedImagePath();
        ColorPixel[][] imagePixels = ImageReader.getImagePixels(new File(manipulatedImagePath));
        int[][] grayscales = GrayscaleConverter.getImageGrayscale(imagePixels);
        return getOneDimensionalVector(grayscales);
    }

    /**
     * Converts a two dimensional array of grayscale values to one dimensional double vector.
     *
     * @param grayscale The grayscale to transform.
     */
    public static double [] getOneDimensionalVector(int [][]grayscales) {
        double [] measures = new double[grayscales.length * grayscales[0].length];
        int idx = 0;
        for (int i = 0; i < grayscales.length; i++) {
            for (int j = 0; j < grayscales[0].length; j++) {
                measures[idx++] = grayscales[i][j];
            }
        }
        return measures;
    }
}
