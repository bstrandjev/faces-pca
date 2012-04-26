package com.borisp.faces.neural.perceptrons;

/**
 * a class representing sigma perceptron.
 *
 * @author Boris
 */
public class SigmaPerceptron extends Perceptron {

    public SigmaPerceptron(int numEntrances) {
        super(numEntrances);
    }

    @Override
    public double getOutput(double[] entranceValues) {
        return 1.0 / (1.0 + Math.exp(-super.getOutput(entranceValues)));
    }
}
