package com.borisp.faces.classifiers.neural_network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.borisp.faces.classifiers.ClassifierInterface;
import com.borisp.faces.classifiers.Example;

/**
 * A class implementing the double-layered back propagation algorithm
 *
 * @author Boris
 */
public class DoubleLayeredNeuralNetwork implements ClassifierInterface {

    private Perceptron[] inputPerceptrons;
    private Perceptron[] middlePerceptrons;
    private Perceptron[] outputPerceptrons;

    private double eta;
    private double inertia;

    private double[] maximumMeasures;
    private double[] minimumMeasures;

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
            int outputPerceptronNumber, double eta, double inertia) {
        this.eta = eta;
        this.inertia = inertia;

        allocatePerceptrons(inputPerceptronNumber, middlePerceptronNumber, outputPerceptronNumber);
    }

    @Override
    public void learnExamples(Example[] examples) {
        for (int i = 0; i < 10000; i++) {
            //randomShuffleArray(trainingSet);
            initializeNormalizationFactors(examples);
            for (Example example : examples) {
                learnExample(example);
            }
        }
    }

    private static void randomShuffleArray(Object [] array) {
        List<Object> list = new ArrayList<Object>();
        for (Object obj: array) {
            list.add(obj);
        }
        Collections.shuffle(list);
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
    }
    private void initializeNormalizationFactors(Example[] examples) {
        this.minimumMeasures = new double[inputPerceptrons.length];
        this.maximumMeasures = new double[inputPerceptrons.length];

        for (int i = 0; i < maximumMeasures.length; i++) {
            minimumMeasures[i] = Double.POSITIVE_INFINITY;
            maximumMeasures[i] = Double.NEGATIVE_INFINITY;
        }

        for (Example example : examples) {
            for (int i = 0; i < minimumMeasures.length; i++) {
                minimumMeasures[i] = Math.min(minimumMeasures[i], example.measures[i]);
                maximumMeasures[i] = Math.max(maximumMeasures[i], example.measures[i]);
            }
        }
    }

    @Override
    public int classifyExample(Example example) {
        setOutputs(example.measures);

        double maxm = Double.NEGATIVE_INFINITY;
        int maxIdx = -1;
        for (int i = 0; i < outputPerceptrons.length; i++) {
            if (outputPerceptrons[i].output > maxm) {
                maxm = outputPerceptrons[i].output;
                maxIdx = i;
            }
        }
        return maxIdx;
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

        double[] outputVector = new double[outputPerceptrons.length];
        for (int i = 0; i < outputVector.length; i++) {
            outputVector[i] = 0.1;
        }
        outputVector[example.classification] = 0.9;

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
            tempV[0] = (input[i] - minimumMeasures[i]) / (maximumMeasures[i] - minimumMeasures[i]);
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
