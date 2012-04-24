package com.borisp.faces.database;

import java.io.File;
import java.io.IOException;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.Image;

/**
 * This class contains methods that will take the initial versions of the training set pictures and
 * record them in the DB.
 *
 * @author Boris
 */
public class InitialRecorder {
    private static final String INITIAL_IMAGE_DIRECTORY = "images" + File.separator + "initial";

    public void recordNewInitialImages(SessionFactory sessionFactory) throws IOException {
        File initialImagesFolder = new File(INITIAL_IMAGE_DIRECTORY);
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (File initialImage : initialImagesFolder.listFiles()) {
            Image image = new Image();
            image.setImagePath(initialImage.getPath());
            image.setKey(initialImage.getName());

            session.save(image);
            System.out.println("Adding: " + image.getKey());
        }
        session.getTransaction().commit();
        System.out.println("Initial recording completed.");
    }
}
