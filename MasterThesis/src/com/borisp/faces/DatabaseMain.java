package com.borisp.faces;

import java.io.IOException;
import java.text.ParseException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ImageGroup;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.database.ClassificationDatabaseHelper;
import com.borisp.faces.database.DatabaseHelper;
import com.borisp.faces.database.InitialRecorder;
import com.borisp.faces.database.ManipulationCreator;
import com.borisp.faces.database.PcaDatabaseHelper;
import com.borisp.faces.weka.WekaContentCreator;

public class DatabaseMain {
    public static void main(String[] args) throws IOException, ParseException {
        // SessionFactory sessionFactory = initializeSessionFactory();
        // LongClassifierExperimenter longClassifierExperimenter = new LongClassifierExperimenter();
        // longClassifierExperimenter.executeExperiments(sessionFactory);
        // new BasicFrame();
        SessionFactory sessionFactory = initializeSessionFactory();
        String imageGroupKey = "turkey";
        doInitialRecording(sessionFactory, imageGroupKey);
        executeManipulation(sessionFactory, true, imageGroupKey);
        // doPcaTransform(sessionFactory);
        // demonstrateTransformation(sessionFactory);
        // demonstrateProjection(sessionFactory);
        // recordClassification(sessionFactory);
        // generateWekaInput(sessionFactory);
        // neuralExperiment(sessionFactory);
    }

    @SuppressWarnings("deprecation")
    private static SessionFactory initializeSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Classification.class);
        configuration.addAnnotatedClass(EigenFaceEntity.class);
        configuration.addAnnotatedClass(Image.class);
        configuration.addAnnotatedClass(ImageGroup.class);
        configuration.addAnnotatedClass(ManipulatedImage.class);
        configuration.addAnnotatedClass(Manipulation.class);
        configuration.addAnnotatedClass(PcaCoeficient.class);
        configuration.addAnnotatedClass(Transformation.class);
        configuration.addAnnotatedClass(User.class);

        configuration.configure("hibernate.cfg.xml");

        return configuration.buildSessionFactory();
    }

    private static void doInitialRecording(SessionFactory sessionFactory, String imageGroupKey)
            throws IOException {
        InitialRecorder initialRecorder = new InitialRecorder();
        ImageGroup imageGroup = DatabaseHelper.getImageGroupByKey(sessionFactory, imageGroupKey);
        initialRecorder.recordNewInitialImages(sessionFactory, imageGroup);
    }

    private static void executeManipulation(SessionFactory sessionFactory,
            boolean createNewManipulation, String imageGroupKey) {
        ManipulationCreator manipulationCreator = new ManipulationCreator();
        ImageGroup imageGroup = DatabaseHelper.getImageGroupByKey(sessionFactory, imageGroupKey);
        manipulationCreator
                .createNewManipulation(sessionFactory, createNewManipulation, imageGroup);
    }

    private static void doPcaTransform(SessionFactory sessionFactory) {
        PcaDatabaseHelper pcaDatabaseHelper = new PcaDatabaseHelper();
        pcaDatabaseHelper.conductPcaTransform(sessionFactory);
    }

    private static void recordClassification(SessionFactory sessionFactory) {
        ClassificationDatabaseHelper classificationHelper = new ClassificationDatabaseHelper();
        classificationHelper.recordClassification("rand", 1, sessionFactory);
    }

    private static void generateWekaInput(SessionFactory sessionFactory) throws IOException {
        WekaContentCreator.generateWekaInput("rand", 7, sessionFactory);
    }
}
