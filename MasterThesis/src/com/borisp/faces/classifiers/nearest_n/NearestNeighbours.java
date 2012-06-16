package com.borisp.faces.classifiers.nearest_n;

import java.util.Arrays;

import com.borisp.faces.classifiers.ClassifierInterface;
import com.borisp.faces.classifiers.examples.Example;

/**
 * A class implementing the nearest neighbor algorithm.
 * <p>
 * The number of neighbors to consider is experimentally determined.
 *
 * @author Boris
 */
public class NearestNeighbours extends NearestNeighborBase implements ClassifierInterface {
    protected int k;
    protected int numClasses;

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
    protected int classifyExampleHelper(Example example, boolean ignoreFirst) {
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
}
