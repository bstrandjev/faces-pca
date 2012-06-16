package com.borisp.faces.classifiers;

import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.classifiers.examples.ComplexExample;
import com.borisp.faces.classifiers.examples.Example;

/**
 * Uses paired tests to compare the classification precision algorithm prior and after PCA
 * transformation.
 *
 * @author Boris
 */
public abstract class AlgorithmComparator extends ClassifierExperimenter {
    private static final int VERIFICATION_EXAMPLE_COUNT = 30;

    /** Conducts experiment using any of the classifiers defined in {@link Classifiers}. */
    @Override
    public void evaluateClassifier(String username, int transformationId,
            SessionFactory sessionFactory, Classifiers[] classifiers, int numberOfExperiments,
            int countedEigenFaces) {
        List<ComplexExample> examples = ClassifierInputPreparator.generateClassifierComplexInput(
                username, transformationId, countedEigenFaces, sessionFactory);
        evaluateClassifierHelper(examples, classifiers, numberOfExperiments, countedEigenFaces);
    }

    private void evaluateClassifierHelper(List<ComplexExample> examples, Classifiers[] classifiers,
            int numberOfExperiments, int countedEigenFaces) {
        this.rand = reinitializeRandom();
        this.countedEigenFaces = countedEigenFaces;
        double totalInitialPrecision = 0;
        double totalPcaPrecision = 0;
        double totalDelta = 0;
        double [] deltas = new double [numberOfExperiments];

        for (int o = 0; o < numberOfExperiments; o++) {
            // Construct the inputs for the different algorithms
            Example[] initialTrainingSet = new Example[examples.size() - VERIFICATION_EXAMPLE_COUNT];
            Example[] pcaTrainingSet = new Example[examples.size() - VERIFICATION_EXAMPLE_COUNT];
            Example[] initialVerificationSet = new Example[VERIFICATION_EXAMPLE_COUNT];
            Example[] pcaVerificationSet = new Example[VERIFICATION_EXAMPLE_COUNT];
            chooseDifferentAlgorithmsInputs(examples, initialTrainingSet, pcaTrainingSet,
                    initialVerificationSet, pcaVerificationSet);

            // Calculate precisions
            double initialPrecision = conductExperiment(initialTrainingSet, initialVerificationSet,
                    classifiers);
            totalInitialPrecision += initialPrecision;
            double pcaPrecision = conductExperiment(pcaTrainingSet, pcaVerificationSet, classifiers);
            totalPcaPrecision += pcaPrecision;

            appendToOutput(String.format("Initial: %.7f Pca: %.7f\n", initialPrecision, pcaPrecision));
            deltas[o] = (1.0 - pcaPrecision) - (1.0 - initialPrecision);
            totalDelta += deltas[o];
        }
        totalInitialPrecision /= numberOfExperiments;
        totalPcaPrecision /= numberOfExperiments;
        totalDelta /= numberOfExperiments;
        appendToOutput(String.format("The total initial precision is: %.7f", totalInitialPrecision));
        appendToOutput(String.format("The total pca precision is: %.7f", totalPcaPrecision));
        appendToOutput(String.format("The total delta is: %.7f", totalDelta));

        double sd = 0;
        for (int i = 0; i < numberOfExperiments; i++) {
            sd += (totalDelta - deltas[i]) * (totalDelta - deltas[i]);
        }
        sd /= numberOfExperiments * (numberOfExperiments - 1);
        sd = Math.sqrt(sd);

        appendToOutput(String.format("The calculated number is: %.7f\n", totalDelta / sd));
    }

    /**
     * Chooses the inputs for the different algorithms
     *
     * @param examples The array of examples from which to choose from
     * @param initialTrainingSet out parameter
     * @param pcaTrainingSet out parameter
     * @param initialVerificationSet out parameter
     * @param pcaVerificationSet out parameter
     */
    private void chooseDifferentAlgorithmsInputs(List<ComplexExample> examples,
            Example[] initialTrainingSet, Example[] pcaTrainingSet,
            Example[] initialVerificationSet, Example[] pcaVerificationSet) {
        ComplexExample[] trainingSet = new ComplexExample[examples.size()
                - VERIFICATION_EXAMPLE_COUNT];
        ComplexExample[] verificationSet = new ComplexExample[VERIFICATION_EXAMPLE_COUNT];
        chooseProportionalExperimentSets(examples, trainingSet, verificationSet);

        for (int i = 0; i < trainingSet.length; i++) {
            initialTrainingSet[i] = new Example();
            pcaTrainingSet[i] = new Example();
            initialTrainingSet[i].measures = trainingSet[i].initialMeasures;
            initialTrainingSet[i].classification = trainingSet[i].classification;
            pcaTrainingSet[i].measures = trainingSet[i].pcaMeasures;
            pcaTrainingSet[i].classification = trainingSet[i].classification;
        }

        for (int i = 0; i < verificationSet.length; i++) {
            initialVerificationSet[i] = new Example();
            pcaVerificationSet[i] = new Example();
            initialVerificationSet[i].measures = verificationSet[i].initialMeasures;
            initialVerificationSet[i].classification = verificationSet[i].classification;
            pcaVerificationSet[i].measures = verificationSet[i].pcaMeasures;
            pcaVerificationSet[i].classification = verificationSet[i].classification;
        }
    }
}
