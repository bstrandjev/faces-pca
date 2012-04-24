package com.borisp.faces;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.EigenFace;
import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.database.InitialRecorder;
import com.borisp.faces.database.ManipulationCreator;

public class DatabaseMain {
    public static void main(String[] args) throws IOException {
        SessionFactory sessionFactory = initializeSessionFactory();
        executeManipulation(sessionFactory, false);
    }

    @SuppressWarnings("deprecation")
    private static SessionFactory initializeSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Classification.class);
        configuration.addAnnotatedClass(EigenFace.class);
        configuration.addAnnotatedClass(Image.class);
        configuration.addAnnotatedClass(ManipulatedImage.class);
        configuration.addAnnotatedClass(Manipulation.class);
        configuration.addAnnotatedClass(PcaCoeficient.class);
        configuration.addAnnotatedClass(Transformation.class);
        configuration.addAnnotatedClass(User.class);

        configuration.configure("hibernate.cfg.xml");

        return configuration.buildSessionFactory();
    }

    private static void doInitialRecording(SessionFactory sessionFactory) throws IOException {
        InitialRecorder initialRecorder = new InitialRecorder();
        initialRecorder.recordNewInitialImages(sessionFactory);
    }

    private static void executeManipulation(SessionFactory sessionFactory,
            boolean createNewManipulation) {
        ManipulationCreator manipulationCreator = new ManipulationCreator();
        manipulationCreator.createNewManipulation(sessionFactory, createNewManipulation);
    }

    private static void doPcaTransform(SessionFactory sessionFactory) {

    }
}