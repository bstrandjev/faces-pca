package com.borisp.faces.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;

/**
 * Auxiliary class defining methods to ease the database access.
 *
 * @author Boris
 */
public class DatabaseHelper {

    /** A method for fetching manipulation based on manipulation index. */
    public static Manipulation getManipulationByIndex(int manipulationIndex,
            SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("from Manipulation m where m.manipulationIndex = :index");
        query.setInteger("index", manipulationIndex);
        return (Manipulation) query.uniqueResult();
    }

    /** A method for fetching user based on his username. */
    public static User getUserByUsername(String username, SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query =
                session.createQuery("from User u where u.name = :username");
        query.setString("username", username);
        return (User) query.uniqueResult();
    }

    /** Retrieves the transformation from the database having the given id */
    public static Transformation getTransformationById(int transformationId,
            SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query =
                session.createQuery("from Transformation t where t.transformationId = :id");
        query.setInteger("id", transformationId);
        return (Transformation) query.uniqueResult();
    }

    /** Retrieves all the classifications of given user for given manipulation. */
    public static List<Classification> getNeededClassifications(User user,
            Manipulation manipulation, SessionFactory sessionFactory) {

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<ManipulatedImage> manipulatedImages = manipulation.getManipulatedImages();
        List<Classification> toReturn = new ArrayList<Classification>();
        for (ManipulatedImage manipulatedImage : manipulatedImages) {
            for (Classification classification:  manipulatedImage.getClassifications()) {
                if (classification.getUser().getName().equals(user.getName())) {
                    toReturn.add(classification);
                }
            }
        }

        return toReturn;
    }

    /**
     * Retrieves all the pca coeficients of image from a single transformation.
     * The coeficients are sorted in decreasing order of the associated eigen value.
     */
    public static List<PcaCoeficient> getPcaCoeficients(ManipulatedImage manipulatedImage,
            Transformation transformation, SessionFactory sessionFactory) {

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<PcaCoeficient> pcaCoeficients = manipulatedImage.getPcaCoeficients();
        List<EigenFaceEntity> eigenFaces = transformation.getEigenFaces();
        // sorting the eigen faces in descending order of the eigen values
        Collections.sort(eigenFaces, Collections.reverseOrder());

        List<PcaCoeficient> toReturn = new ArrayList<PcaCoeficient>();
        for (EigenFaceEntity eigenFace : eigenFaces) {
            for (PcaCoeficient pcaCoeficient : pcaCoeficients) {
                if (pcaCoeficient.getEigenFace().getEigenFaceId()
                        .equals(eigenFace.getEigenFaceId())) {
                    toReturn.add(pcaCoeficient);
                    break;
                }
            }
        }

        return toReturn;
    }
}
