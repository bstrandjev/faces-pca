package com.borisp.faces;

import java.io.IOException;
import java.text.ParseException;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.ImageGroup;
import com.borisp.faces.database.ClassificationDatabaseHelper;
import com.borisp.faces.database.DatabaseHelper;
import com.borisp.faces.database.InitialRecorder;
import com.borisp.faces.database.ManipulationCreator;
import com.borisp.faces.database.PcaDatabaseHelper;
import com.borisp.faces.ui.BasicFrame;
import com.borisp.faces.weka.WekaContentCreator;

public class DatabaseMain {
    public static void main(String[] args) throws IOException, ParseException {
        // SessionFactory sessionFactory = initializeSessionFactory();
        // LongClassifierExperimenter longClassifierExperimenter = new LongClassifierExperimenter();
        // longClassifierExperimenter.executeExperiments(sessionFactory);
         new BasicFrame();
//        SessionFactory sessionFactory = BasicFrame.initializeSessionFactory();
//        String imageGroupKey = "turkey";
//        doInitialRecording(sessionFactory, imageGroupKey);
//        executeManipulation(sessionFactory, true, imageGroupKey);
//         doPcaTransform(sessionFactory);
        // demonstrateTransformation(sessionFactory);
        // demonstrateProjection(sessionFactory);
        // recordClassification(sessionFactory);
        // generateWekaInput(sessionFactory);
        // neuralExperiment(sessionFactory);
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
        pcaDatabaseHelper.transformSelectedManipulations(sessionFactory, 1, 2, 3, 4);
    }

    private static void recordClassification(SessionFactory sessionFactory) {
        ClassificationDatabaseHelper classificationHelper = new ClassificationDatabaseHelper();
        classificationHelper.recordClassification("rand", 1, sessionFactory);
    }

    private static void generateWekaInput(SessionFactory sessionFactory) throws IOException {
        WekaContentCreator.generateWekaInput("rand", 7, 1, sessionFactory);
    }
}
