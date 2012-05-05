package com.borisp.faces.recognizer;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.classifiers.Example;
import com.borisp.faces.database.DatabaseHelper;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.pca.PcaTransformer;

/**
 * Evaluates the recognizing abilities of the system.
 *
 * @author Boris
 */
public abstract class RecognizerExperimenter {

    /** The transformation to use in the evaluation. */
    private Transformation transformation;
    /** The manipulation to use in the evaluation. */
    private Manipulation manipulation;

    /** Conducts experiment using the neural network. */
    public void evaluateRecognizer(int transformationId, SessionFactory sessionFactory,
            double noiseLevel, int countedEigenFaces) {
        this.transformation = DatabaseHelper
                .getTransformationById(transformationId, sessionFactory);
        this.manipulation = transformation.getManipulation();
        Example[] recognizerInput = generateRecognizerInput(transformationId, countedEigenFaces,
                sessionFactory);
        NearestNeighbourRecognizer recognizer = new NearestNeighbourRecognizer(recognizerInput);
        PcaTransformer pcaTransformer = new PcaTransformer(ImageScaler.TARGET_HEIGHT,
                ImageScaler.TARGET_WIDTH, transformation);

        int idx = 0;
        int correctlyClassified = 0;
        int wronglyClassified = 0;
        for (ManipulatedImage image : manipulation.getManipulatedImages()) {
            int[][] imageWithNoise = NoiseGenerator.getImageWithNoise(image, noiseLevel);
            double[] pcaCoeficients = pcaTransformer.getPcaCoeficients(imageWithNoise);
            Example example = new Example();
            example.measures = Arrays.copyOf(pcaCoeficients, countedEigenFaces);
            example.classification = idx;
            int classification = recognizer.classifyExample(example);
            if (classification == idx) {
                correctlyClassified++;
            } else {
                wronglyClassified++;
            }
            idx++;
        }
        appendToOutput("Correctly classified: " + correctlyClassified + "\n");
        appendToOutput("Wrongly classified: " + wronglyClassified + "\n");
        appendToOutput("Total precision: " + (double)correctlyClassified / (double)idx + "\n");
    }

    /**
     * Creates an array of {@link Example}s for all classifications of a user for transformation.
     *
     * @param transformationId The if of the transformation to use to transform the input
     * @param countedEigenFaces The number of dimensions which to include in the analysis
     * @param sessionFactory A session factory to use for the database connections.
     */
    public Example[] generateRecognizerInput(int transformationId, int countedEigenFaces,
            SessionFactory sessionFactory) {

        Example[] examples = new Example[manipulation.getManipulatedImages().size()];
        for (int i = 0; i < examples.length; i++) {
            List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                    manipulation.getManipulatedImages().get(i), transformation, sessionFactory);
            examples[i] = new Example();
            examples[i].measures = new double[countedEigenFaces];
            for (int j = 0; j < countedEigenFaces; j++) {
                examples[i].measures[j] = pcaCoeficients.get(j).getCoeficient();
            }
            examples[i].classification = i;
        }
        return examples;
    }

    abstract protected void appendToOutput(String string);
}
