package com.borisp.faces.classifiers.nearest_n;

import java.util.Arrays;

import com.borisp.faces.classifiers.ClassifierInterface;
import com.borisp.faces.classifiers.Example;

/**
 * A class implementing the nearest neighbor algorithm.
 * <p>
 * The number of neighbors to consider is experimentally determined.
 *
 * @author Boris
 */
public class NearestNeighbours implements ClassifierInterface {
    private int k;
    private Example [] examples;
    private int numClasses;

    /** Auxiliary class that should allow for finding the nearest points. */
    private class VectorToClassifiedPoint implements Comparable<VectorToClassifiedPoint> {
        private double distance;
        private int classification;

        public VectorToClassifiedPoint(Example from, Example to) {
            this.distance = distance(from, to);
            this.classification = to.classification;
        }

        @Override
        public int compareTo(VectorToClassifiedPoint o) {
            return Double.compare(distance, o.distance);
        }
    }

    /**
     * @param numClasses The number of classes of classification.
     */
    public NearestNeighbours(int numClasses) {
        this.numClasses = numClasses;
    }

    @Override
    public void learnExamples(Example[] examples) {
        this.examples = examples;
        int bestK = 0;
        int bestCnt = 0;
        for (int i = 1; i < examples.length - 1; i++) {
            int cnt = 0;
            this.k = i;
            for (Example example : examples) {
                if (classifyExampleHelper(example, true) == example.classification) {
                    cnt++;
                }
            }
            if (cnt > bestCnt) {
                bestCnt = cnt;
                bestK = i;
            }
        }
        this.k = bestK;
    }

    @Override
    public int classifyExample(Example example) {
        return classifyExampleHelper(example, false);
    }

    /** A auxiliary function for classifying example. */
    private int classifyExampleHelper(Example example, boolean ignoreFirst) {
        VectorToClassifiedPoint [] points = new VectorToClassifiedPoint[examples.length];
        for (int i = 0; i < examples.length; i++) {
            points[i] = new VectorToClassifiedPoint(example, examples[i]);
        }
        Arrays.sort(points);
        int [] counts = new int[numClasses];
        for (int i = ignoreFirst ? 1 : 0; i < k; i++) {
            counts[points[i].classification]++;
        }
        int bestIdx = 0;
        int bestCnt = counts[0];
        for (int i = 1; i < numClasses; i++) {
            if (bestCnt < counts[i]) {
                bestCnt = counts[i];
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    /** Calculates the distance between to examples. */
    private double distance(Example ex1, Example ex2) {
        double sum = 0;
        for (int i = 0; i < ex1.measures.length; i++) {
            sum += (ex1.measures[i] - ex2.measures[i]) * (ex1.measures[i] - ex2.measures[i]);
        }
        return Math.sqrt(sum);
    }
}
