package com.borisp.faces.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;

import com.borisp.faces.beans.ClassifiedImage;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.ImageGroup;
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
    /** Query for selecting manipulation based on manipulation index, */
    private static final String SELECT_MANIPULATIONS_BY_INDICES_SQL_QUERY =
            "from Manipulation m where m.manipulationIndex in (:indices)";
    /** A string for selecting all the transformations from the database. */
    private static final String SELECT_ALL_TRANSFORMATIONS_SQL_QUERY = "from Transformation t";
    /** A string for selecting all the users from the database. */
    private static final String SELECT_ALL_USERS_SQL_QUERY = "from User u";
    /** A string for selecting all the image groups from the database. */
    private static final String SELECT_ALL_IMAGE_GROUPS_SQL_QUERY = "from ImageGroup ig";
    /** A string for selecting all the image groups from the database. */
    private static final String SELECT_IMAGE_GROUPS_BY_KEY_SQL_QUERY =
            "from ImageGroup ig where ig.key = :key";

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
    public static List<ClassifiedImage> getNeededClassifications(User user,
            List<ManipulatedImage> manipulatedImages, SessionFactory sessionFactory) {

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<ClassifiedImage> toReturn = new ArrayList<ClassifiedImage>();
        for (ManipulatedImage manipulatedImage : manipulatedImages) {
            for (ClassifiedImage classification:  manipulatedImage.getClassifications()) {
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

    /** Returns the last manipulation created in the database. */
    public static Integer getLastManipulationIndex(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(Manipulation.class).setProjection(
                Projections.max("manipulationIndex"));
        return (Integer) criteria.uniqueResult();
    }

    /**
     * Returns all the good manipulated images from the database result of the given manipulations.
     *
     * @param sessionFactory The session factory to use.
     * @param manipulationIndices The indices of the manipulations to consider.
     * @return List containing all the good manipulated images.
     */

    public static List<ManipulatedImage> getGoodManipulationImages(SessionFactory sessionFactory,
            Integer... manipulationIndices) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery(SELECT_MANIPULATIONS_BY_INDICES_SQL_QUERY);
        query.setParameterList("indices", manipulationIndices);
        @SuppressWarnings("unchecked")
        Iterator<Manipulation> iterator = query.iterate();
        List<ManipulatedImage> goodManipulatedImages = new ArrayList<ManipulatedImage>();
        while (iterator.hasNext()) {
            List<ManipulatedImage> manipulatedImages = iterator.next().getManipulatedImages();
            for (ManipulatedImage manipulatedImage : manipulatedImages) {
                if (manipulatedImage.getIsGood() != null && manipulatedImage.getIsGood() != 0) {
                    goodManipulatedImages.add(manipulatedImage);
                }
            }
        }
        return goodManipulatedImages;
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

    /** Returns all the image groups in the database. */
    public static List<ImageGroup> getAllImageGroups(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_IMAGE_GROUPS_SQL_QUERY);
        List<ImageGroup> imageGroups = new ArrayList<ImageGroup>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            imageGroups.add((ImageGroup) it.next());
        }
        return imageGroups;
    }

    /** Returns image group from the database based on its key. */
    public static ImageGroup getImageGroupByKey(SessionFactory sessionFactory, String key) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_IMAGE_GROUPS_BY_KEY_SQL_QUERY);
        query.setString("key", key);
        List<ImageGroup> imageGroups = new ArrayList<ImageGroup>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            imageGroups.add((ImageGroup) it.next());
        }
        return imageGroups.isEmpty() ? null : imageGroups.get(0);
    }

    /** Returns the first transformation from the DB. Throws if no such exists. */
    public static Transformation getFirstTransformation(SessionFactory sessionFactory) {
        return getAllTransformations(sessionFactory).get(0);
    }

    /** Returns the first image group from the DB. Throws if no such exists. */
    public static ImageGroup getFirstImageGroup(SessionFactory sessionFactory) {
        return getAllImageGroups(sessionFactory).get(0);
    }
}
