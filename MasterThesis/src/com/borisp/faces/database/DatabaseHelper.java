package com.borisp.faces.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

    // Query constants
    /** A string for selecting all the manipulations from the database. */
    private static final String SELECT_ALL_MANIPULATIONS_SQL_QUERY = "from Manipulation m";
    /** A string for selecting all the transformations from the database. */
    private static final String SELECT_ALL_TRANSFORMATIONS_SQL_QUERY = "from Transformation t";
    /** A string for selecting all the users from the database. */
    private static final String SELECT_ALL_USERS_SQL_QUERY = "from User u";

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

    /** Returns all the manipulations in the database. */
    public static List<Manipulation> getAllManipulations(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_MANIPULATIONS_SQL_QUERY);
        List<Manipulation> manipulations = new ArrayList<Manipulation>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            manipulations.add((Manipulation) it.next());
        }
        return manipulations;
    }

    /** Returns all the transformations in the database. */
    public static List<Transformation> getAllTransformations(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_TRANSFORMATIONS_SQL_QUERY);
        List<Transformation> transformations = new ArrayList<Transformation>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            transformations.add((Transformation) it.next());
        }
        return transformations;
    }

    /** Returns all the users in the database. */
    public static List<User> getAllUsers(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_USERS_SQL_QUERY);
        List<User> users = new ArrayList<User>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            users.add((User) it.next());
        }
        return users;
    }

    /** Returns the first transformation from the DB> Throws if no such exists. */
    public static Transformation getFirstTransformation(SessionFactory sessionFactory) {
        return getAllTransformations(sessionFactory).get(0);
    }
}
