package com.borisp.faces.neural.perceptrons;

import java.util.Random;

/**
 * A class representing simple perceptron for neural networks.
 *
 * @author Boris
 */
public class Perceptron {
    private static final Random rand = new Random();

    public double[] weights;
    public double[] delta;
    public double output;
    public double error;

    /** Constructs the perceptron assigning random weights for the input edges. */
    public Perceptron(int numEntrances) {
        weights = new double[numEntrances + 1]; // including a bias
        delta = new double[numEntrances + 1];

        for (int i = 0; i < numEntrances + 1; i++) {
            weights[i] = initialValueForW();
        }
    }

    /** Retrieves the output from the perceptron based on the input values. */
    public double getOutput(double[] entranceValues) {
        if (weights.length != entranceValues.length + 1) {
            System.err.println("When evaluating value of perceptron: "
                    + "the entrance size does not match the weight size");
            throw new IllegalArgumentException("Size mismatch!!");
        }

        double ans = 0.0;
        for (int i = 0; i < entranceValues.length; i++)
            ans += weights[i] * entranceValues[i];
        ans += weights[weights.length - 1];
        return ans;
    }

    /** Sets the output of the perceptron based on the getOutput method. */
    public void setOutput(double[] entranceValues) {
        this.output = getOutput(entranceValues);
    }

    /** A function used to initialize the weights of the perceptron. */
    private static double initialValueForW() {
        return (-0.05 + rand.nextDouble() * 0.1);
    }
}
