package com.borisp.faces.classifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;

import com.borisp.faces.classifiers.identity.IdentityClassifier;
import com.borisp.faces.classifiers.nearest_n.CorrelationNearestNeighbours;
import com.borisp.faces.classifiers.nearest_n.NearestNeighbours;
import com.borisp.faces.classifiers.nearest_n.WeightedNearestNeighbours;
import com.borisp.faces.classifiers.neural_network.DoubleLayeredNeuralNetwork;

/**
 * An auxiliary class for the neural network experiments.
 *
 * @author Boris
 */
public abstract class ClassifierExperimenter {
    public static enum Classifiers {
        NEURAL_NETWORK("Neural network"),
        NEAREST_NEIGHBOURS("Nearest neighbor"),
        WEIGHTED_NEAREST_NEIGHBOURS("Weighted nn"),
        CORRELATED_NEAREST_NEIGHBOURS("Correlated nn"),
        IDENTITY("Perfect classifier");

        private String label;
        private Classifiers(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final int OUTPUT_CLASSES = 2;
    private static final int RAND_SEED = 123434;
    // Neural network constants
    private static final int VERIFICATION_EXAMPLE_COUNT = 20;
    private static final double ETA = 0.2;
    private static final double INERTIA = 0.05;

    private int countedEigenFaces;
    private Random rand;

    /** Conducts experiment using any of the classifiers defined in {@link Classifiers}. */
    public void evaluateClassifier(String username, int transformationId,
            SessionFactory sessionFactory, Classifiers[] classifiers, int numberOfExperiments,
            int countedEigenFaces) {
        List<Example> examples = ClassifierInputPreparator.generateClassifierInput(username,
                transformationId, countedEigenFaces, sessionFactory);
        evaluateClassifierHelper(examples, classifiers, numberOfExperiments, countedEigenFaces);
    }

    /** Conducts experiment using any of the classifiers defined in {@link Classifiers}. */
    public void evaluateClassifier(List<Example> inExamples, Classifiers[] classifiers,
            int numberOfExperiments, int countedEigenFaces) {

        List<Example> examples = new ArrayList<Example>();
        for (int i = 0; i < inExamples.size(); i++) {
            Example example = new Example();
            example.classification = inExamples.get(i).classification;
            example.measures = Arrays.copyOf(inExamples.get(i).measures, countedEigenFaces);
            examples.add(example);
        }

        evaluateClassifierHelper(examples, classifiers, numberOfExperiments, countedEigenFaces);
    }


    private void evaluateClassifierHelper(List<Example> examples, Classifiers[] classifiers,
            int numberOfExperiments, int countedEigenFaces) {
        this.rand = new Random(RAND_SEED);
        this.countedEigenFaces = countedEigenFaces;
        double totalPrecision = 0;
        for (int o = 0; o < numberOfExperiments; o++) {
            Collections.shuffle(examples, rand);
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
        appendToOutput(String.format("The total precision is: %.7f\n", totalPrecision
                / numberOfExperiments));

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

    private ClassifierInterface getClassifierInstance(Classifiers classifier) {
        switch (classifier) {
        case NEURAL_NETWORK:
            int middleLayeredCount = 3;
            if (countedEigenFaces >= 9) {
                middleLayeredCount++;
            }
            if (countedEigenFaces >= 15) {
                middleLayeredCount++;
            }
            if (countedEigenFaces >= 20) {
                middleLayeredCount++;
            }
            return new DoubleLayeredNeuralNetwork(countedEigenFaces, middleLayeredCount,
                    OUTPUT_CLASSES, ETA, INERTIA);
        case NEAREST_NEIGHBOURS:
            return new NearestNeighbours(OUTPUT_CLASSES);
        case WEIGHTED_NEAREST_NEIGHBOURS:
            return new WeightedNearestNeighbours(OUTPUT_CLASSES);
        case CORRELATED_NEAREST_NEIGHBOURS:
            return new CorrelationNearestNeighbours(OUTPUT_CLASSES);
        case IDENTITY:
            return new IdentityClassifier();
        }
        return null;
    }

    abstract protected void appendToOutput(String string);
}
