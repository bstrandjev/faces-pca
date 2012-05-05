package com.borisp.faces.classifiers.nearest_n;

import com.borisp.faces.classifiers.Example;

/**
 * A base class used for all nearest neighbor algorithms based on euclidian distance.
 *
 * @author Boris
 */
public class NearestNeighborBase {
    protected Example [] examples;

    /** Auxiliary class that should allow for finding the nearest points. */
    protected class VectorToClassifiedPoint implements Comparable<VectorToClassifiedPoint> {
        public double distance;
        public int classification;

        public VectorToClassifiedPoint(Example from, Example to) {
            this.distance = distance(from, to);
            this.classification = to.classification;
        }

        @Override
        public int compareTo(VectorToClassifiedPoint o) {
            return Double.compare(distance, o.distance);
        }
    }

    /** Calculates the distance between to examples. */
    protected double distance(Example ex1, Example ex2) {
        double sum = 0;
        for (int i = 0; i < ex1.measures.length; i++) {
            sum += (ex1.measures[i] - ex2.measures[i]) * (ex1.measures[i] - ex2.measures[i]);
        }
        return Math.sqrt(sum);
    }
}
