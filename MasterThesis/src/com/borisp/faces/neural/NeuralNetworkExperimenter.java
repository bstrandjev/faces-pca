package com.borisp.faces.neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;

/**
 * An auxiliary class for the neural network experiments.
 *
 * @author Boris
 */
public class NeuralNetworkExperimenter {

    private static final int NUMBER_OF_EXPERIMENTS = 6;
    private static final int COUNTED_EIGEN_FACES = 6;
    private static final int MIDDLE_LAYER_PERCEPTRONS = 3;
    private static final int OUTPUT_LAYER_PERCEPTRONS = 2;
    private static final int VERIFICATION_EXAMPLE_COUNT = 20;
    private static final double ETA = 0.2;
    private static final double INERTIA = 0.05;

    /** Conducts experiment using the neural network. */
    public static void evaluateNetwork(String username, int transformationId,
            SessionFactory sessionFactory) {
        List<Example> examples = NeuralNetworkPreparator.generateExamplesForNeuralNetwork(username,
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
            totalPrecision += conductExperiment(trainingSet, verificationSet);
        }
        System.out.println("The total precision is: " + totalPrecision / NUMBER_OF_EXPERIMENTS);
    }

    /** Conducts a single experiment and returns the precision of its classification. */
    private static double conductExperiment(Example [] trainingSet, Example [] verificationSet) {
        DoubleLayeredNeuralNetwork network = new DoubleLayeredNeuralNetwork(COUNTED_EIGEN_FACES,
                MIDDLE_LAYER_PERCEPTRONS, OUTPUT_LAYER_PERCEPTRONS, ETA, INERTIA);
        for (int i = 0; i < 10000; i++) {
            //randomShuffleArray(trainingSet);
            network.learnExamples(trainingSet);
        }
        int [][] classifiedCnt = new int [OUTPUT_LAYER_PERCEPTRONS][OUTPUT_LAYER_PERCEPTRONS];
        for (Example example : verificationSet) {
            int tmpClassification = example.classification;
            classifiedCnt[tmpClassification][network.classifyExample(example)]++;
        }
        int good = 0;
        int all = 0;
        for (int i = 0; i < OUTPUT_LAYER_PERCEPTRONS; i++) {
            for (int j = 0; j < OUTPUT_LAYER_PERCEPTRONS; j++) {
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

    private static void randomShuffleArray(Object [] array) {
        List<Object> list = new ArrayList<Object>();
        for (Object obj: array) {
            list.add(obj);
        }
        Collections.shuffle(list);
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
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
