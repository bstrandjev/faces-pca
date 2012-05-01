package com.borisp.faces.classifiers;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.classifiers.identity.IdentityClassifier;
import com.borisp.faces.classifiers.nearest_n.NearestNeighbours;
import com.borisp.faces.classifiers.neural_network.DoubleLayeredNeuralNetwork;

/**
 * An auxiliary class for the neural network experiments.
 *
 * @author Boris
 */
public abstract class ClassifierExperimenter {
    private static final int NUMBER_OF_EXPERIMENTS = 20;
    private static final int COUNTED_EIGEN_FACES = 9;
    private static final int OUTPUT_CLASSES = 2;

    public static enum Classifiers {
        NEURAL_NETWORK, NEAREST_NEIGHBOURS, IDENTITY;
    }

    // Neural network constants
    private static final int MIDDLE_LAYER_PERCEPTRONS = 4;
    private static final int VERIFICATION_EXAMPLE_COUNT = 20;
    private static final double ETA = 0.2;
    private static final double INERTIA = 0.05;

    /** Conducts experiment using the neural network. */
    public void evaluateClassifier(String username, int transformationId,
            SessionFactory sessionFactory, Classifiers [] classifiers) {
        List<Example> examples = ClassifierInputPreparator.generateClassifierInput(username,
                transformationId, COUNTED_EIGEN_FACES, sessionFactory);

        double totalPrecision = 0;
        for (int o = 0; o < NUMBER_OF_EXPERIMENTS; o++) {
            Collections.shuffle(examples);
            Example [] trainingSet = new Example[examples.size() - VERIFICATION_EXAMPLE_COUNT];
            Example [] verificationSet = new Example[VERIFICATION_EXAMPLE_COUNT];
            for (int i = 0; i < examples.size(); i++) {
                if (i < trainingSet.length) {
                    trainingSet[i] = examples.get(i);
                } else {
                    verificationSet[i - trainingSet.length] = examples.get(i);
                }
            }
            totalPrecision += conductExperiment(trainingSet, verificationSet, classifiers);
        }
        appendToOutput("The total precision is: " + totalPrecision / NUMBER_OF_EXPERIMENTS + "\n");
    }

    /**
     * Conducts a single experiment and returns the precision of its classification.
     * <p>
     * Experiment with every given classifier type is conducted and a selection based on the
     * majoritarity vote is done.
     *
     * @param trainingSet The training set to use.
     * @param verificationSet The verification set to use.
     * @param classifierTypes The classifier types which to experiment with.
     * @return The precision found during the experiment.
     */
    private double conductExperiment(Example[] trainingSet, Example[] verificationSet,
            Classifiers [] classifierTypes) {
        ClassifierInterface [] classifiers = new ClassifierInterface[classifierTypes.length];
        int idx = 0;
        for (Classifiers classifierType : classifierTypes) {
            classifiers[idx] = getClassifierInstance(classifierType);
            classifiers[idx].learnExamples(trainingSet);
            idx++;
        }
        int [][] classifiedCnt = new int [OUTPUT_CLASSES][OUTPUT_CLASSES];
        for (Example example : verificationSet) {
            int tmpClassification = example.classification;
            int [] cnts = new int[OUTPUT_CLASSES];
            for (ClassifierInterface classifier: classifiers) {
                cnts[classifier.classifyExample(example)]++;
            }
            int maxCnt = -1;
            int maxIdx = -1;
            for (int i = 0; i < OUTPUT_CLASSES; i++) {
                if (cnts[i] > maxCnt) {
                    maxCnt = cnts[i];
                    maxIdx = i;
                }
            }
            classifiedCnt[tmpClassification][maxIdx]++;
        }
        int good = 0;
        int all = 0;
        for (int i = 0; i < OUTPUT_CLASSES; i++) {
            for (int j = 0; j < OUTPUT_CLASSES; j++) {
                if (i == j) {
                    good += classifiedCnt[i][j];
                }
                all += classifiedCnt[i][j];
                appendToOutput(String.format("%3d ", classifiedCnt[i][j]));
            }
            appendToOutput("\n");
        }
        double precision = (double)good / (double)all;
        appendToOutput("Precision: " + precision + "\n");
        return precision;
    }

    private static ClassifierInterface getClassifierInstance(Classifiers classifier) {
        switch (classifier) {
        case NEURAL_NETWORK:
            return new DoubleLayeredNeuralNetwork(COUNTED_EIGEN_FACES, MIDDLE_LAYER_PERCEPTRONS,
                    OUTPUT_CLASSES, ETA, INERTIA);
        case NEAREST_NEIGHBOURS:
            return new NearestNeighbours(COUNTED_EIGEN_FACES);
        case IDENTITY:
            return new IdentityClassifier();
        }
        return null;
    }

    abstract protected void appendToOutput(String string);
}
