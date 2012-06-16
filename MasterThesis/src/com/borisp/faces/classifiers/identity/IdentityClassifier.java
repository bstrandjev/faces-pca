package com.borisp.faces.classifiers.identity;

import com.borisp.faces.classifiers.ClassifierInterface;
import com.borisp.faces.classifiers.examples.Example;

/**
 * Return the real example classification without any calculations.
 * <p>
 * Will be used to evaluate the combination of several combined classifiers.
 *
 * @author Boris
 */
public class IdentityClassifier implements ClassifierInterface {

    @Override
    public void learnExamples(Example[] examples) {
        // nothing to do here.
    }

    @Override
    public int classifyExample(Example example) {
        return example.classification;
    }

}
