package com.borisp.faces.classifiers;

import com.borisp.faces.classifiers.examples.Example;


/**
 * A interface for the classifiers.
 *
 * @author Boris
 */
public interface ClassifierInterface {
    /**
     * Pass the classifier the training examples.
     *
     * @param examples the examples to use for training.
     */
    public void learnExamples(Example[] examples);

    /**
     * Returns the estimated classification of the given example (also in its corresponding field).
     *
     * @param example The example which to classify. The classification field will be ignored.
     * @return The classification of the given example.
     */
    public int classifyExample(Example example);
}
