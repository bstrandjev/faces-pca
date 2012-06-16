package com.borisp.faces.recognizer;

import java.util.Arrays;

import com.borisp.faces.classifiers.examples.Example;
import com.borisp.faces.classifiers.nearest_n.NearestNeighborBase;

/**
 * A class that can be used to recognize objects based on nearest neighbor algorithm.
 *
 * @author Boris
 */
public class NearestNeighbourRecognizer extends NearestNeighborBase {

    /**
     * @param examples The examples of the training set. Their classifications should coincide with
     *        their indices.
     */
    public NearestNeighbourRecognizer(Example[] examples) {
        this.examples = examples;
    }

    /** Uses the nearest neighbor to find the classification of the nearest example. */
    public int classifyExample(Example example) {
        VectorToClassifiedPoint [] points = new VectorToClassifiedPoint[examples.length];
        for (int i = 0; i < examples.length; i++) {
            points[i] = new VectorToClassifiedPoint(example, examples[i]);
        }
        Arrays.sort(points);
        return points[0].classification;
    }
}
