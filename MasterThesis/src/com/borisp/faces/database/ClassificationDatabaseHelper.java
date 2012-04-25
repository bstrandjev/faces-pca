package com.borisp.faces.database;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.User;
import com.borisp.faces.classification.Face;
import com.borisp.faces.classification.JsonParser;
import com.borisp.faces.util.IOHelper;

public class ClassificationDatabaseHelper {
    private static final String CLASSIFICATION_JSON_FILE_PATTERN =
            "users\\%s\\manipulation_%02d\\classification.json";
    private User user;

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
                    Classification classification = new Classification();
                    classification.setIsBeautiful((byte)(faces[i].getBeautiful() ? 1 : 0));
                    classification.setManipulatedImage(manipulatedImage);
                    classification.setUser(user);
                    session.save(classification);
                    break;
                }
            }
        }
        session.getTransaction().commit();
    }
}
