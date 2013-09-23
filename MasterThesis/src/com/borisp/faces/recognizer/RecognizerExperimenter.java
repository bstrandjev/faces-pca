package com.borisp.faces.recognizer;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.classifiers.ClassifierInputPreparator;
import com.borisp.faces.classifiers.examples.Example;
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

    /** Conducts a recognizer experiment on both non-transformed image and pca image. */
    public void evaluateRecognizer(int transformationId, SessionFactory sessionFactory,
            double noiseLevel, int countedEigenFaces) {
        this.transformation = DatabaseHelper
                .getTransformationById(transformationId, sessionFactory);
        Example[] pcaRecognizerInput = generateRecognizerPcaInput(transformationId,
                countedEigenFaces, sessionFactory);
        Example[] initialRecognizerInput = generateRecognizerManipulationInput(transformationId,
                sessionFactory);
        NearestNeighbourRecognizer pcaRecognizer = new NearestNeighbourRecognizer(
                pcaRecognizerInput);
        NearestNeighbourRecognizer initialRecognizer = new NearestNeighbourRecognizer(
                initialRecognizerInput);

        PcaTransformer pcaTransformer = new PcaTransformer(ImageScaler.TARGET_HEIGHT,
                ImageScaler.TARGET_WIDTH, transformation);

        int idx = 0;
        int pcaCorrectlyClassified = 0;
        int pcaWronglyClassified = 0;
        int initialCorrectlyClassified = 0;
        int initialWronglyClassified = 0;
        for (ManipulatedImage image : transformation.getAllManipulatedImages()) {
            int[][] imageWithNoise = NoiseGenerator.getImageWithNoise(image, noiseLevel);
            double[] pcaCoeficients = pcaTransformer.getPcaCoeficients(imageWithNoise);
            Example pcaExample = new Example();
            pcaExample.measures = Arrays.copyOf(pcaCoeficients, countedEigenFaces);
            pcaExample.classification = idx;
            int pcaClassification = pcaRecognizer.classifyExample(pcaExample);
            if (pcaClassification == idx) {
                pcaCorrectlyClassified++;
            } else {
                pcaWronglyClassified++;
            }
            Example initialExample = new Example();
            initialExample.measures = ClassifierInputPreparator
                    .getOneDimensionalVector(imageWithNoise);
            initialExample.classification = idx;
            int initialClassification = initialRecognizer.classifyExample(initialExample);
            if (initialClassification == idx) {
                initialCorrectlyClassified++;
            } else {
                initialWronglyClassified++;
            }
            appendToOutput("" + pcaClassification + " " + initialClassification + "\n");
            idx++;
        }
        appendToOutput("---Direct recognition statistics: ----\n");
        appendToOutput("Correctly classified: " + initialCorrectlyClassified + "\n");
        appendToOutput("Wrongly classified: " + initialWronglyClassified + "\n");
        appendToOutput("Total precision: " + (double) initialCorrectlyClassified / (double) idx
                + "\n");

        appendToOutput("---PCA statistics: ----\n");
        appendToOutput("Correctly classified: " + pcaCorrectlyClassified + "\n");
        appendToOutput("Wrongly classified: " + pcaWronglyClassified + "\n");
        appendToOutput("Total precision: " + (double) pcaCorrectlyClassified / (double) idx + "\n");
    }

    /**
     * Creates an array of {@link Example}s for all classifications of a user for transformation.
     *
     * @param transformationId The if of the transformation to use to transform the input
     * @param countedEigenFaces The number of dimensions which to include in the analysis
     * @param sessionFactory A session factory to use for the database connections.
     */
    public Example[] generateRecognizerPcaInput(int transformationId, int countedEigenFaces,
            SessionFactory sessionFactory) {

        List<ManipulatedImage> manipulatedImages = transformation.getAllManipulatedImages();

        Example[] examples = new Example[manipulatedImages.size()];
        for (int i = 0; i < examples.length; i++) {
            List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                    manipulatedImages.get(i), transformation, sessionFactory);
            examples[i] = new Example();
            examples[i].measures = new double[countedEigenFaces];
            for (int j = 0; j < countedEigenFaces; j++) {
                examples[i].measures[j] = pcaCoeficients.get(j).getCoeficient();
            }
            examples[i].classification = i;
        }
        return examples;
    }

    /**
     * Creates an array of {@link Example}s for all classifications of a user for transformation.
     *
     * @param transformationId The if of the transformation to use to transform the input
     * @param countedEigenFaces The number of dimensions which to include in the analysis
     * @param sessionFactory A session factory to use for the database connections.
     */
    public Example[] generateRecognizerManipulationInput(int transformationId,
            SessionFactory sessionFactory) {

        List<ManipulatedImage> manipulatedImages = transformation.getAllManipulatedImages();
        Example[] examples = new Example[manipulatedImages.size()];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = new Example();
            examples[i].measures = ClassifierInputPreparator.getInitialMeasures(manipulatedImages
                    .get(i));
            examples[i].classification = i;
        }
        return examples;
    }

    abstract protected void appendToOutput(String string);
}
