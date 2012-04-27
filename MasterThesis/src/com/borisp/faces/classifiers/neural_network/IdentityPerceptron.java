package com.borisp.faces.classifiers.neural_network;

/**
 * A class representing identity perceptron.
 *
 * @author Boris
 */
public class IdentityPerceptron extends Perceptron {

    public IdentityPerceptron() {
        super(1);
    }

    @Override
    public double getOutput(double[] entranceValues) {
        if (entranceValues.length != 1) {
            System.err.println("Identity perceptron can be called only with a single entry value");
            throw new IllegalArgumentException("Indentity perceptron with multiple entrances!!");
        }
        return entranceValues[0];
    }
}
