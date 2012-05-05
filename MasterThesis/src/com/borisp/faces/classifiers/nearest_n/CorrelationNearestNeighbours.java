package com.borisp.faces.classifiers.nearest_n;

import com.borisp.faces.classifiers.Example;

/**
 * Implements nearest neighbors with correlation distance.
 *
 * @author Boris
 */
public class CorrelationNearestNeighbours extends NearestNeighbours {

    public CorrelationNearestNeighbours(int numClasses) {
        super(numClasses);
    }

    @Override
    protected double distance(Example ex1, Example ex2) {
        double mean1 = 0.0;
        double mean2 = 0.0;
        int n = ex1.measures.length;
        for (int i = 0; i < n; i++) {
            mean1 += ex1.measures[i];
            mean2 += ex2.measures[i];
        }
        mean1 /= n;
        mean2 /= n;
        double nom = 0.0;
        double sigma1 = 0.0, sigma2 = 0.0;
        for (int i = 0; i < n; i++) {
            nom += (ex1.measures[i] - mean1) * (ex2.measures[i] - mean2);
            sigma1 += (ex1.measures[i] - mean1) * (ex1.measures[i] - mean1);
            sigma2 += (ex2.measures[i] - mean1) * (ex2.measures[i] - mean1);
        }

        return nom / Math.sqrt(sigma1 * sigma2);
    }
}
