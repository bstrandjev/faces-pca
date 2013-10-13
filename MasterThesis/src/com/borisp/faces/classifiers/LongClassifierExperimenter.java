package com.borisp.faces.classifiers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ClassifiedImage;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.classifiers.ClassifierExperimenter.Classifiers;
import com.borisp.faces.classifiers.examples.Example;
import com.borisp.faces.database.ClassificationDatabaseHelper;
import com.borisp.faces.database.DatabaseHelper;

/**
 * A class that can be used to evaluate the precision of specific classifier over all possible
 * dimension counts.
 *
 * @author Boris
 */
public class LongClassifierExperimenter {
    /** The file in which all the results will be recorded. */
    private static final String OUTPUT_FILE_PATH = "experiments" + File.separator
            + "nationality_experiments" + File.separator + "experiment.out";
    // Output constants
    private static final int NAME_MAX_LENGTH = 12;
    private static final int RESULT_MAX_LENGTH = 12;
    private static final int PRECISION = 4;
    // Regex constant
    private static final String SOUGHT_FOR_STRING = "The total precision is: ";
    private static final int NUMBER_OF_EXPERIMENTS = 40;
    // Restriction constants
    private static final int TEST_START_INDEX = 1;
    private static final int TEST_LIMIT_INDEX = 20;

    /** The file in which output is to be added. */
    private FileWriter outputFile;

    /** A list containing all the users in the database. */
    private List<Classification> classifications;
    /** The transformation on which to evaluate. */
    private Transformation transformation;
    /** The number of faces in the set associated with the transformation. */
    private int numberOfFaces;
    /** A cache making it possible to cache the example calculation. */
    private Map<String, List<Example>> classificationExamples;
    /** The key of the classification if the long runner is to run for specific classification. */
    private String classificationKey;

    /** The experimenter to use for all the experiments. */
    private ClassifierExperimenter classifierExperimenter = new ClassifierExperimenter() {
        @Override
        protected void appendToOutput(String string) {
            if (string.startsWith(SOUGHT_FOR_STRING)) {
                String precisionStr = string.substring(SOUGHT_FOR_STRING.length()).trim();
                double precision = Double.valueOf(precisionStr);
                try {
                    outputFile.append(String.format(
                            "%" + RESULT_MAX_LENGTH + "." + PRECISION + "f", precision * 100.0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public LongClassifierExperimenter() throws IOException {
        File outputFile = new File(OUTPUT_FILE_PATH);
        outputFile.getParentFile().mkdirs();
        this.outputFile = new FileWriter(outputFile);
        this.classificationExamples = new HashMap<String, List<Example>>();
    }

    public LongClassifierExperimenter(String classificationKey) throws IOException {
        this();
        this.classificationKey = classificationKey;
    }

    /** Executes all experiments on all training sets using all classifiers. */
    public void executeExperiments(SessionFactory sessionFactory) throws IOException {
        intializeFields(sessionFactory);
        prepareExampleCache(sessionFactory);
        for (Classifiers classifier : Classifiers.values()) {
            if (classifier.equals(Classifiers.IDENTITY)
                    /*|| !classifier.equals(Classifiers.NEURAL_NETWORK)*/) {
                continue;
            }
            solveForClassifier(classifier, sessionFactory);
        }
        outputFile.flush();
        outputFile.close();
    }

    private void intializeFields(SessionFactory sessionFactory) {
        if (classificationKey != null) {
            this.classifications = new ArrayList<Classification>();
            Classification classification =
                    ClassificationDatabaseHelper.getClassificationByKey(sessionFactory, classificationKey);
            this.classifications.add(classification);
            this.transformation = DatabaseHelper.findAppropriateTransformation(sessionFactory,
                    classification);
        } else {
            this.classifications = ClassificationDatabaseHelper
                    .getAllClassifications(sessionFactory);
            this.transformation = DatabaseHelper.getFirstTransformation(sessionFactory);
        }
        this.numberOfFaces = transformation.getAllManipulatedImages().size();
    }

    /** Runs the experiments for the given classifier only ranging the dimensions and input sets. */
    private void solveForClassifier(Classifiers classifier, SessionFactory sessionFactory)
            throws IOException {
        outputFile.append("\n");
        outputFile.append("Classifier: " + classifier.getLabel());
        outputFile.append("\n");
        outputFile.append("Train set");
        for (int i = 0; i < NAME_MAX_LENGTH - 9; i++) {
            outputFile.append(" ");
        }
        for (int i = TEST_START_INDEX; i <= Math.min(TEST_LIMIT_INDEX, numberOfFaces); i++) {
            outputFile.append(String.format("%" + RESULT_MAX_LENGTH + "d", i));
        }
        outputFile.append("\n");

        Classifiers[] classifiers = { classifier };
        for (Classification classification : classifications) {
            String key = classification.getClassificationKey();
            outputFile.append(key);
            for (int i = 0; i < NAME_MAX_LENGTH - key.length(); i++) {
                outputFile.append(" ");
            }
            for (int i = TEST_START_INDEX; i <= Math.min(TEST_LIMIT_INDEX, numberOfFaces); i++) {
                System.out.print(i + " ");
                classifierExperimenter.evaluateClassifier(classificationExamples.get(key),
                        classifiers, NUMBER_OF_EXPERIMENTS, i);
            }
            System.out.println();
            outputFile.append("\n");
        }
    }

    /** Prepares the cache of the user examples */
    private void prepareExampleCache(SessionFactory sessionFactory) {
        for (Classification classification : classifications) {
            List<ClassifiedImage> classifications = DatabaseHelper.getNeededClassifications(
                    classification, transformation.getAllManipulatedImages(), sessionFactory);

            List<Example> examples = new ArrayList<Example>();
            for (ClassifiedImage classifiedImage : classifications) {
                List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                        classifiedImage.getManipulatedImage(), transformation, sessionFactory);
                Example example = new Example();
                example.measures = new double[numberOfFaces];
                for (int i = 0; i < numberOfFaces; i++) {
                    example.measures[i] = pcaCoeficients.get(i).getCoeficient();
                }
                example.classification = classifiedImage.getClassificationValue()
                        .getClassificationValueId();
                examples.add(example);
            }
            classificationExamples.put(classification.getClassificationKey(), examples);
        }
    }
}
