package com.borisp.faces.classifiers.nearest_n;

import java.util.Arrays;

import com.borisp.faces.classifiers.Example;

/**
 * A class implementing weighted nearest neighbors algorithm
 *
 * @author Boris
 */
public class WeightedNearestNeighbours extends NearestNeighbours {

    public WeightedNearestNeighbours(int numClasses) {
        super(numClasses);
    }

    /** A auxiliary function for classifying example. */
    @Override
    protected int classifyExampleHelper(Example example, boolean ignoreFirst) {
        VectorToClassifiedPoint [] points = new VectorToClassifiedPoint[examples.length];
        for (int i = 0; i < examples.length; i++) {
            points[i] = new VectorToClassifiedPoint(example, examples[i]);
        }
        Arrays.sort(points);
        double [] values = new double[numClasses];
        for (int i = ignoreFirst ? 1 : 0; i < k; i++) {
            values[points[i].classification] += 1.0 / Math.sqrt((points[i].distance + 1e-9));
        }
        int bestIdx = 0;
        double bestValue = values[0];
        for (int i = 1; i < numClasses; i++) {
            if (bestValue < values[i]) {
                bestValue = values[i];
                bestIdx = i;
            }
        }
        return bestIdx;
    }

}
