package com.borisp.faces;

import java.io.IOException;
import java.text.ParseException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.classifiers.LongClassifierExperimenter;
import com.borisp.faces.database.ClassificationDatabaseHelper;
import com.borisp.faces.database.InitialRecorder;
import com.borisp.faces.database.ManipulationCreator;
import com.borisp.faces.database.PcaDatabaseHelper;
import com.borisp.faces.weka.WekaContentCreator;

public class DatabaseMain {
    public static void main(String[] args) throws IOException, ParseException {
        SessionFactory sessionFactory = initializeSessionFactory();
        LongClassifierExperimenter longClassifierExperimenter = new LongClassifierExperimenter();
        longClassifierExperimenter.executeExperiments(sessionFactory);
//        new BasicFrame();
//        SessionFactory sessionFactory = initializeSessionFactory();
//        doPcaTransform(sessionFactory);
//        demonstrateTransformation(sessionFactory);
//        demonstrateProjection(sessionFactory);
//        recordClassification(sessionFactory);
//        generateWekaInput(sessionFactory);
//        neuralExperiment(sessionFactory);
    }

    @SuppressWarnings("deprecation")
    private static SessionFactory initializeSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Classification.class);
        configuration.addAnnotatedClass(EigenFaceEntity.class);
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
