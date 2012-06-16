package com.borisp.faces.classifiers.naive_baies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.borisp.faces.classifiers.ClassifierInterface;
import com.borisp.faces.classifiers.examples.Example;

/**
 * A class implementing the naive Baies classifier.
 *
 * @author Boris
 */
public class NaiveBaies implements ClassifierInterface {
    private static final double EPSILON = 1e-9;

    private class DiscretisedExample {
        private int [] measures;
        private int classification;
    }

    private final int NUM_SEGMENTS;
    private double [][] thresholds;
    private DiscretisedExample[] discretisedExamples;
    /** A set containing all possible classifications as determined from the educational set. */
    private Set<Integer> possibleClassifications;

    public NaiveBaies(int numSegments) {
        this.NUM_SEGMENTS = numSegments;
    }

    @Override
    public void learnExamples(Example[] examples) {
        deduceThresholds(examples);
        this.discretisedExamples = new DiscretisedExample[examples.length];
        this.possibleClassifications = new HashSet<Integer>();
        for (int i = 0; i < examples.length; i++) {
            discretisedExamples[i] = new DiscretisedExample();
            discretisedExamples[i].measures = new int[examples[0].measures.length];
            discretisedExamples[i].classification = examples[i].classification;
            possibleClassifications.add(examples[i].classification);
            for (int j = 0; j < examples[i].measures.length; j++) {
                discretisedExamples[i].measures[j] = discretiseMeasure(j, examples[i].measures[j]);
            }
        }
    }

    @Override
    public int classifyExample(Example example) {
        double maxm = -1;
        int bestClassification = -1;
        for (Integer classification : possibleClassifications) {
            double probability = 1.0;
            for (int i = 0; i < example.measures.length; i++) {
                int discreteValue = discretiseMeasure(i, example.measures[i]);
                int count = 0;
                for (DiscretisedExample discretisedExample : discretisedExamples) {
                    if (discretisedExample.measures[i] == discreteValue
                            && discretisedExample.classification == classification) {
                        count++;
                    }
                }
                probability *= (double)count / (double)discretisedExamples.length;
            }
            if (probability > maxm) {
                maxm = probability;
                bestClassification = classification;
            }
        }
        return bestClassification;
    }

    /**
     * In this function we deduce the upper bound thresholds for each measure's segment.
     * These thresholds are used to discretise the values of the continuous measures.
     */
    private void deduceThresholds(Example[] examples) {
        thresholds = new double[examples[0].measures.length][NUM_SEGMENTS];

        // Here we determine how many measures happen in each segment
        int [] counts = new int [NUM_SEGMENTS];
        int num = examples.length;
        int curIdx = 0;
        while (num > 0) {
            counts[curIdx]++;
            curIdx = (curIdx + 1) % NUM_SEGMENTS;
            num--;
        }

        // Here we deduce the actual thresholds
        for (int i = 0; i < examples[0].measures.length; i++) {
            double [] measures = new double [examples.length];
            int idx = 0;
            for (Example example : examples) {
                measures[idx++] = example.measures[i];
            }
            Arrays.sort(measures);
            idx = -1;
            for (int j = 0; j < counts.length; j++) {
                idx += counts[j];
                thresholds[i][j] = measures[idx];
            }
        }
    }

    /** Converts a double value to its discrete equivalent.
     * This function requires {@link #deduceThresholds(Example[])} to be called as precondition.
     *
     * @param measureIdx The index of the dimension for which discretisation is needed.
     * @param value The value to discretise.
     * @return The discrete equivalent - integer between 0...{@link NaiveBaies#NUM_SEGMENTS}.
     */
    private int discretiseMeasure(int measureIdx, double value) {
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            if (value < thresholds[measureIdx][i] + EPSILON) {
                return i;
            }
        }
        return NUM_SEGMENTS - 1;
    }
}
