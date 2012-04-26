package com.borisp.faces.neural;

import java.util.List;

import com.borisp.faces.neural.perceptrons.IdentityPerceptron;
import com.borisp.faces.neural.perceptrons.Perceptron;
import com.borisp.faces.neural.perceptrons.SigmaPerceptron;

/**
 * A class implementing the double-layered back propagation algorithm
 *
 * @author Boris
 */
public class DoubleLayeredNeuralNetwork implements NeuralNetwork {

    private Perceptron[] inputPerceptrons;
    private Perceptron[] middlePerceptrons;
    private Perceptron[] outputPerceptrons;

    private double eta;
    private double inertia;

    /**
     * (re-)initializes the neural network with the given number of perceptrons.
     * <p>
     * All previous propagation effects will be erased with this operation.
     *
     * @param inputPerceptrons The number of input perceptrons to be used for the network.
     * @param middlePerceptrons The number of middle-layer perceptrons to be used for the network
     * @param eta The speed with which to apply the changes according to the new samples
     * @param inertia The inertia to use during the learning algorithm
     */
    public DoubleLayeredNeuralNetwork(int inputPerceptronNumber, int middlePerceptronNumber,
            double eta, double inertia) {
        this.eta = eta;
        this.inertia = inertia;

        allocatePerceptrons(inputPerceptronNumber, middlePerceptronNumber, 2);

    }

    @Override
    public void learnExamples(List<Example> examples) {
        for (Example example : examples) {
            learnExample(example);
        }
    }

    @Override
    public boolean classifyExample(Example example) {
        setOutputs(example.measures);

        double maxm = Double.NEGATIVE_INFINITY;
        int maxIdx = -1;
        for (int i = 0; i < outputPerceptrons.length; i++) {
            if (outputPerceptrons[i].output > maxm) {
                maxm = outputPerceptrons[i].output;
                maxIdx = i;
            }
        }
        return maxIdx == 0;
    }

    /** Allocates all the arrays that will be used in the process of learning. */
    private void allocatePerceptrons(int inputNumber, int middleNumber, int outputNumber) {

        this.inputPerceptrons = new IdentityPerceptron[inputNumber];
        this.middlePerceptrons = new SigmaPerceptron[middleNumber];
        this.outputPerceptrons = new SigmaPerceptron[outputNumber];

        for (int i = 0; i < inputNumber; i++) {
            inputPerceptrons[i] = new IdentityPerceptron();
        }

        for (int i = 0; i < middleNumber; i++) {
            middlePerceptrons[i] = new SigmaPerceptron(inputNumber);
        }

        for (int i = 0; i < outputNumber; i++) {
            outputPerceptrons[i] = new SigmaPerceptron(middleNumber);
        }
    }

    /** Inserts a single example in the neural network. */
    private void learnExample(Example example) {
        setOutputs(example.measures);

        double[] outputVector = { 0.1, 0.1 };
        if (example.classification) {
            outputVector[0] = 0.9;
        } else {
            outputVector[1] = 0.9;
        }

        setErrors(outputVector);
        modifyWeights();
    }

    /** Sets the output values for the topmost level of the network based on the given input. */
    private void setOutputs(double[] input) {
        if (input.length != inputPerceptrons.length) {
            System.err.println("Backpropagation::setOutputs different sizes");
            throw new IllegalArgumentException(
                    "Inputs do not match number input perceptrons in set outputs");
        }

        double[] inputsOut = new double[inputPerceptrons.length];
        double[] tempV = new double[1];
        for (int i = 0; i < inputPerceptrons.length; i++) {
            tempV[0] = input[i];
            inputPerceptrons[i].setOutput(tempV);
            inputsOut[i] = inputPerceptrons[i].output;
        }

        double[] middleOut = new double[middlePerceptrons.length];
        for (int i = 0; i < middlePerceptrons.length; i++) {
            middlePerceptrons[i].setOutput(inputsOut);
            middleOut[i] = middlePerceptrons[i].output;
        }

        for (int i = 0; i < outputPerceptrons.length; i++) {
            outputPerceptrons[i].setOutput(middleOut);
        }
    }

    /** Updates the errors of all the perceptrons based on the given expected output. */
    private void setErrors(double[] output) {
        if (output.length != outputPerceptrons.length) {
            System.err.println("Backpropagation::setErrors different sizes");
            throw new IllegalArgumentException(
                    "Ouputs do not match number output perceptrons in set errors");
        }

        for (int i = 0; i < outputPerceptrons.length; i++) {
            outputPerceptrons[i].error = outputPerceptrons[i].output
                    * (1.0 - outputPerceptrons[i].output)
                    * (output[i] - outputPerceptrons[i].output);
        }

        for (int i = 0; i < middlePerceptrons.length; i++) {
            double sum = 0.0;

            for (int j = 0; j < outputPerceptrons.length; j++) {
                sum += outputPerceptrons[j].weights[i] * outputPerceptrons[j].error;
            }

            middlePerceptrons[i].error = sum * (1.0 - middlePerceptrons[i].output)
                    * middlePerceptrons[i].output;
        }
    }

    /** Modifies the weights according to the newly inserted example. */
    private void modifyWeights() {
        for (int j = 0; j < middlePerceptrons.length; j++) {
            for (int i = 0; i < inputPerceptrons.length; i++) {
                middlePerceptrons[j].delta[i] = eta * middlePerceptrons[j].error
                        * inputPerceptrons[i].output + inertia * middlePerceptrons[j].delta[i];
                middlePerceptrons[j].weights[i] += middlePerceptrons[j].delta[i];
            }

            middlePerceptrons[j].delta[inputPerceptrons.length] = eta * middlePerceptrons[j].error;
            middlePerceptrons[j].weights[inputPerceptrons.length] +=
                    middlePerceptrons[j].delta[inputPerceptrons.length];
        }

        for (int j = 0; j < outputPerceptrons.length; j++) {
            for (int i = 0; i < middlePerceptrons.length; i++) {
                outputPerceptrons[j].delta[i] = eta * outputPerceptrons[j].error
                        * middlePerceptrons[i].output + inertia * outputPerceptrons[j].delta[i];
                outputPerceptrons[j].weights[i] += outputPerceptrons[j].delta[i];
            }

            outputPerceptrons[j].delta[middlePerceptrons.length] = eta * outputPerceptrons[j].error;
            outputPerceptrons[j].weights[middlePerceptrons.length] +=
                    outputPerceptrons[j].delta[middlePerceptrons.length];
        }
    }
}
