package com.borisp.faces.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.android_ui.Face;
import com.borisp.faces.android_ui.JsonParser;
import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ClassificationValue;
import com.borisp.faces.beans.ClassifiedImage;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.User;
import com.borisp.faces.util.IOHelper;

public class ClassificationDatabaseHelper {

    // QUery strings
    /** Query for selecting classification based on classification key */
    private static final String SELECT_CLASSIFICATION_BY_NAME_SQL_QUERY =
            "from Classification c where c.classificationKey = :key";
    /** Query for selecting all classifications from the database */
    private static final String SELECT_ALL_CLASSIFICATIONS_SQL_QUERY = "from Classification c";

    // Constants
    private static final String CLASSIFICATION_JSON_FILE_PATTERN =
            "users\\%s\\manipulation_%02d\\classification.json";
    private static final String BEAUTIFUL_CLASSIFICATION_KEY = "beauty";

    // Fields
    private User user;

    // Cache for the beauty classification
    private static Classification beautyClassification = null;
    private static ClassificationValue beautyPositiveClassificationValue = null;
    private static ClassificationValue beautyNegativeClassificationValue = null;

    /** Returns all the classifications in the database. */
    public static List<Classification> getAllClassifications(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_CLASSIFICATIONS_SQL_QUERY);
        List<Classification> classifications = new ArrayList<Classification>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            classifications.add((Classification) it.next());
        }
        return classifications;
    }

    /** Returns classification with the given key from the database. */
    public static Classification getClassificationByKey(SessionFactory sessionFactory, String key) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery(SELECT_CLASSIFICATION_BY_NAME_SQL_QUERY);
        query.setString("key", key);
        return (Classification) query.uniqueResult();
    }

    /**
     * Checks whether the given classified image is beautiful or not.
     *
     * @param classifiedImage the classified image to check
     * @param sessionFactory used for executing the queries
     * @return 1 if the classified image for classification 'beauty' and is marked as beautiful. 0
     *         otherwise
     */
    public static int checkClassifiedImageBeautiful(ClassifiedImage classifiedImage,
            SessionFactory sessionFactory) {
        ClassificationValue classificationValue = classifiedImage.getClassificationValue();
        return (getBeautyClassification(sessionFactory).equals(
                classificationValue.getClassification()) && classificationValue
                .equals(getBeautyClassificationPositiveValue(sessionFactory))) ? 1 : 0;
    }

    /** Records the classifications done by the specified user in the database. */
    public void recordClassification(String username, int manipulationIndex,
            SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        this.user = new User();
        user.setName(username);
        session.save(user);
        session.getTransaction().commit();

        Manipulation manipulation =
                DatabaseHelper.getManipulationByIndex(manipulationIndex, sessionFactory);
        String classificationJsonPath = String.format(CLASSIFICATION_JSON_FILE_PATTERN, username,
                manipulation.getManipulationIndex());
        String classificationJson = IOHelper.getFileContent(classificationJsonPath);
        JsonParser jsonParser = new JsonParser();
        Face[] faces = jsonParser.deserializeFaces(classificationJson);

        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (ManipulatedImage manipulatedImage : manipulation.getManipulatedImages()) {
            String imageKey = manipulatedImage.getOriginalImage().getKey();
            String classificationKey = imageKey.substring(0, imageKey.lastIndexOf("."));
            for (int i = 0; i < faces.length; i++) {
                if (faces[i].getKey().equals(classificationKey)) {
                    ClassifiedImage classifiedImage = new ClassifiedImage();
                    if (faces[i].getBeautiful()) {
                        classifiedImage
                                .setClassificationValue(getBeautyClassificationPositiveValue(sessionFactory));
                    } else {
                        classifiedImage
                                .setClassificationValue(getBeautyClassificationNegativeValue(sessionFactory));
                    }
                    classifiedImage.setManipulatedImage(manipulatedImage);
                    classifiedImage.setUser(user);
                    session.save(classifiedImage);
                    break;
                }
            }
        }
        session.getTransaction().commit();
    }

    private static Classification getBeautyClassification(SessionFactory sessionFactory) {
        if (beautyClassification == null) {
            return beautyClassification = getClassificationByKey(sessionFactory,
                    BEAUTIFUL_CLASSIFICATION_KEY);
        } else {
            return beautyClassification;
        }
    }

    private static ClassificationValue getBeautyClassificationPositiveValue(
            SessionFactory sessionFactory) {
        if (beautyPositiveClassificationValue == null) {
            return getBeautyClassificationValueHelper(sessionFactory, "1");
        } else {
            return beautyPositiveClassificationValue = getBeautyClassificationValueHelper(
                    sessionFactory, "1");
        }
    }

    private static ClassificationValue getBeautyClassificationNegativeValue(
            SessionFactory sessionFactory) {
        if (beautyNegativeClassificationValue == null) {
            return getBeautyClassificationValueHelper(sessionFactory, "0");
        } else {
            return beautyNegativeClassificationValue = getBeautyClassificationValueHelper(
                    sessionFactory, "0");
        }
    }

    /** Gets beauty classification value bean based on specific value. */
    private static ClassificationValue getBeautyClassificationValueHelper(
            SessionFactory sessionFactory, String value) {
        Classification beautyClassification = getBeautyClassification(sessionFactory);
        for (ClassificationValue classificationValue : beautyClassification
                .getClassificationValues()) {
            if (value.equals(classificationValue.getValue())) {
                return classificationValue;
            }
        }
        return null;
    }
}
