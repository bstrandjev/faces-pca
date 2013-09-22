package com.borisp.faces.database;

import java.io.File;
import java.io.IOException;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ImageGroup;

/**
 * This class contains methods that will take the initial versions of the training set pictures and
 * record them in the DB.
 *
 * @author Boris
 */
public class InitialRecorder {
    private static final String IMAGE_GROUP_DIRECTORY = "images" + File.separator + "%s";
    private static final String IMAGE_KEY_PATTERN = "%s_%s";

    public void recordNewInitialImages(SessionFactory sessionFactory, ImageGroup imageGroup)
            throws IOException {
        File initialImagesFolder =
                new File(String.format(IMAGE_GROUP_DIRECTORY, imageGroup.getKey()));

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        for (File initialImage : initialImagesFolder.listFiles()) {
            Image image = new Image();
            image.setImagePath(initialImage.getPath());
            String key = String.format(IMAGE_KEY_PATTERN, imageGroup.getKey(), initialImage.getName());
            image.setKey(key);
            image.setImageGroup(imageGroup);

            session.save(image);
            System.out.println("Adding: " + image.getKey());
        }
        session.getTransaction().commit();
        System.out.println("Initial recording completed.");
    }
}
