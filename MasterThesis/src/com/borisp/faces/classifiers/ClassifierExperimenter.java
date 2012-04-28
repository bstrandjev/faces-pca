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
public class ClassifierExperimenter {
    private static final int NUMBER_OF_EXPERIMENTS = 20;
    private static final int COUNTED_EIGEN_FACES = 9;
    private static final int OUTPUT_CLASSES = 2;

    private enum Classifiers {
        NEURAL_NETWORK, NEAREST_NEIGHBOURS, IDENTITY;
    }

    // Neural network constants
    private static final int MIDDLE_LAYER_PERCEPTRONS = 4;
    private static final int VERIFICATION_EXAMPLE_COUNT = 20;
    private static final double ETA = 0.2;
    private static final double INERTIA = 0.05;

    /** Conducts experiment using the neural network. */
    public static void evaluateClassifier(String username, int transformationId,
            SessionFactory sessionFactory) {
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
            totalPrecision += conductExperiment(trainingSet, verificationSet, Classifiers.IDENTITY,
                    Classifiers.NEAREST_NEIGHBOURS, Classifiers.NEURAL_NETWORK);
        }
        System.out.println("The total precision is: " + totalPrecision / NUMBER_OF_EXPERIMENTS);
    }

    /**
     * Conducts a single experiment and returns the precision of its classification.
     * <p>
     * Experiment with every given classifier type is conducted and a selection based on the
     * majoritarity vote is done.
     *
     * @param trainingSet The training set to use.
     * @param verificationSet The verification set to use.
     * @param classifiers The classifier types which to experiment with.
     * @return The precision found during the experiment.
     */
    private static double conductExperiment(Example[] trainingSet, Example[] verificationSet,
            Classifiers... classifierTypes) {
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
                System.out.printf("%3d ", classifiedCnt[i][j]);
            }
            System.out.println();
        }
        double precision = (double)good / (double)all;
        System.out.println("Precision: " + precision);
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

    /** Conducts experiment using the neural network. */
    public static void conductExperiment2(String username, int transformationId,
            SessionFactory sessionFactory) {
        Example[] examples = new Example[8];
        for (int i = 0; i < 8; i++) {
            examples[i] = new Example();
            examples[i].measures = new double[8];
            examples[i].measures[i] = 1.0;
            examples[i].classification = i;
        }
        DoubleLayeredNeuralNetwork network = new DoubleLayeredNeuralNetwork(8,
                3, 8, ETA, INERTIA);
        for (int i = 0; i < 10000; i++) {
            network.learnExamples(examples);
        }
        int goodCnt = 0;
        int badCnt = 0;
        for (Example example : examples) {
            int tmpClassification = example.classification;
            if (tmpClassification == network.classifyExample(example)) {
                goodCnt++;
            } else {
                badCnt++;
            }
        }
        System.out.println("Good examples: " + goodCnt);
        System.out.println("Bad examples: " + badCnt);
    }
}
