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

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ClassifiedImage;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.ImageGroup;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;

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
    private static final String SELECT_MANIPULATED_IMAGES_BY_INDICES_SQL_QUERY =
            "from ManipulatedImage mi where mi.manipulatedImageId in (:ids)";

    /** A method for fetching manipulation based on manipulation index. */
    public static Manipulation getManipulationByIndex(int manipulationIndex,
            SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("from Manipulation m where m.manipulationIndex = :index");
        query.setInteger("index", manipulationIndex);
        return (Manipulation) query.uniqueResult();
    }

    public static Transformation getTransformationById(int transformationId,
            SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query =
                session.createQuery("from Transformation t where t.transformationId = :id");
        query.setInteger("id", transformationId);
        return (Transformation) query.uniqueResult();
    }

    /** Retrieves all the classified images for given manipulation. */
    public static List<ClassifiedImage> getNeededClassifications(Classification classification,
            List<ManipulatedImage> manipulatedImages, SessionFactory sessionFactory) {

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<ClassifiedImage> toReturn = new ArrayList<ClassifiedImage>();
        for (ManipulatedImage manipulatedImage : manipulatedImages) {
            for (ClassifiedImage classifiedImage : manipulatedImage.getClassifications()) {
                String classifiedImageClassificationKey = classifiedImage.getClassificationValue()
                        .getClassification().getClassificationKey();
                if (classifiedImageClassificationKey.equals(classification.getClassificationKey())) {
                    toReturn.add(classifiedImage);
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

    /**
     * Selects manipulated images by the given indices.
     *
     * @param sessionFactory The session factory to use
     * @param manipulatedImageIndices The indices of the manipulated images to use.
     * @return The manipulated images loaded from the database.
     */
    @SuppressWarnings("unchecked")
    public static List<ManipulatedImage> getManipulatedImagesByIndices(
            SessionFactory sessionFactory, List<Integer> manipulatedImageIndices) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery(SELECT_MANIPULATED_IMAGES_BY_INDICES_SQL_QUERY);
        query.setParameter("ids", manipulatedImageIndices);
        return query.list();
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

    /**
     * Finds transformation the DB that transforms all the classified images of the classification
     * @param sessionFactory the session factory to use
     * @param classification The classification for which we search for matching transformation
     * @return The matching classification or null if no such exists.
     */
    public static Transformation findMatchingTransformation(SessionFactory sessionFactory,
            Classification classification) {
        List<Integer> classificationManipulatedImages = classification
                .constructManipualtedImagesIndexList();
        for (Transformation transformation : getAllTransformations(sessionFactory)) {
            List<Integer> transformationManipualtedImages = transformation
                    .constructManipualtedImagesIndexList();
            if (transformationManipualtedImages.equals(classificationManipulatedImages)) {
                return transformation;
            }
        }
        return null;
    }

    /**
     * Finds the matching transformation for a classification, or creates new one if no such exists.
     *
     * @param classification The classification for which to find the appropriate transformation.
     * @return Transformation from the database (might be newly created).
     */
    public static Transformation findAppropriateTransformation(SessionFactory sessionFactory,
            Classification classification) {
        Transformation transformation = DatabaseHelper.findMatchingTransformation(sessionFactory,
                classification);
        if (transformation == null) {
            System.out.println("INFO: Transformation does not exist, will be created");
            System.out.println("INFO: Transformation created");
            PcaDatabaseHelper pcaDatabaseHelper = new PcaDatabaseHelper();
            List<Integer> manipulatedImageIndices = classification
                    .constructManipualtedImagesIndexList();
            transformation = pcaDatabaseHelper.transformSelectedManipulatedImages(sessionFactory,
                    manipulatedImageIndices);
        } else {
            System.out.println("INFO: Transformation already exists, will be reused");
        }
        return transformation;
    }
}
