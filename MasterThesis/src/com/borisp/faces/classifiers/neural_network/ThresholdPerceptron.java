package com.borisp.faces.classifiers.neural_network;

/**
 * A class representing threshold perceptron.
 *
 * @author Boris
 */
public class ThresholdPerceptron extends Perceptron {
    /** The threshold to use. */
    private static final double THRESHOLD = 0.5;

    public ThresholdPerceptron(int numEntrances) {
        super(numEntrances);
    }

    @Override
    public double getOutput(double[] entranceValues) {
        double x = super.getOutput(entranceValues);
        return x > THRESHOLD ? 1.0 : 0.0;
    }

}
