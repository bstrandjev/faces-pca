package com.borisp.faces.neural;


/**
 * A interface for neural network. Currently the supported neural network is with binary output.
 *
 * @author Boris
 */
public interface NeuralNetwork {
    /**
     * Teach the network the given examples. Every example will be inserted multiple times.
     *
     * @param examples the examples to insert in the network.
     */
    public void learnExamples(Example[] examples);

    /**
     * Returns the estimated classification of the given example (also in its corresponding field).
     *
     * @param example The example which to classify. The classification bit will be ignored and
     *        after the method return will be loaded with the estimated classification.
     * @return The classification of the given example.
     */
    public int classifyExample(Example example);
}
