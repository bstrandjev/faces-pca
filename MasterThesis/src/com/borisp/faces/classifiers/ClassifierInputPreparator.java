package com.borisp.faces.classifiers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.database.DatabaseHelper;

public class ClassifierInputPreparator {

    /**
     * Creates an array of {@link Example}s for all classifications of a user for transformation.
     *
     * @param username The username of the user that did the classification to use as input
     * @param sessionFactory A session factory to use for the database connections.
     */
    public static List<Example> generateClassifierInput(String username, int transformationId,
            int countedEigenFaces, SessionFactory sessionFactory) {
        User user = DatabaseHelper.getUserByUsername(username, sessionFactory);
        Transformation transformation =
                DatabaseHelper.getTransformationById(transformationId, sessionFactory);
        Manipulation manipulation = transformation.getManipulation();
        List<Classification> classifications =
                DatabaseHelper.getNeededClassifications(user, manipulation, sessionFactory);

        List<Example> examples = new ArrayList<Example>();
        for (Classification classification : classifications) {
            List<PcaCoeficient> pcaCoeficients = DatabaseHelper.getPcaCoeficients(
                    classification.getManipulatedImage(), transformation, sessionFactory);
            Example example = new Example();
            example = new Example();
            example.measures = new double[countedEigenFaces];
            for (int i = 0; i < countedEigenFaces; i++) {
                example.measures[i] = pcaCoeficients.get(i).getCoeficient();
            }
            example.classification = (classification.getIsBeautiful() == 1) ? 0 : 1;
            examples.add(example);
        }
        return examples;
    }
}
