package com.borisp.faces.database;

import java.io.File;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;

/**
 * This class handles all the database recordings for pca transformation.
 *
 * @author Boris
 */
public class PcaDatabaseHelper {
    /** Does PCA transform on the last manipulation recorded in the database. */
    public static void conductPcaTransform(SessionFactory sessionFactory) {
        File[] imageFiles = getLastManipulationImages(sessionFactory);
    }

    /** Constructs an array containing all the images associated with the last successful manipulation. */
    private static File [] getLastManipulationImages(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(Manipulation.class).setProjection(
                Projections.max("manipulationIndex"));
        Integer maxIndex = (Integer) criteria.uniqueResult();

        Query query =
                session.createQuery("from Manipulation m where m.manipulationIndex = :index");
        query.setInteger("index", maxIndex);
        Manipulation manipulation = (Manipulation) query.uniqueResult();
        List<ManipulatedImage> manipulatedImages = manipulation.getManipulatedImages();
        File [] imageFiles = new File[manipulatedImages.size()];
        for (int i = 0; i < manipulatedImages.size(); i++) {
            imageFiles[i] = new File(manipulatedImages.get(i).getManipulatedImagePath());
        }
        return imageFiles;
    }
}
