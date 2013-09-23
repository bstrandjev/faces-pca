package com.borisp.faces.database;

import java.io.File;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.TransformedImage;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.pca.PcaTransformer;
import com.borisp.faces.pca.PcaTransformer.EigenFace;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;

/**
 * This class handles all the database recordings for pca transformation.
 *
 * @author Boris
 */
public class PcaDatabaseHelper {
    private static final String EGIEN_FACE_FILE_PATTERN =
            "images/transformation_%02d/eigen_faces/face%03d.jpg";

    private static final String AVERAGE_FACE_FILE_PATTERN =
            "images/transformation_%02d/average_image.jpg";

    private Integer transformationIndex;

    public void transformSelectedManipulations(SessionFactory sessionFactory,
            Integer... manipulationIndices) {
        List<ManipulatedImage> manipulatedImages =
                DatabaseHelper.getGoodManipulationImages(sessionFactory, manipulationIndices);
        conductPcaHelper(sessionFactory, manipulatedImages);
    }

    public void transformLastManipulation(SessionFactory sessionFactory) {
        List<ManipulatedImage> manipulatedImages = getLastManipulationImages(sessionFactory);
        conductPcaHelper(sessionFactory, manipulatedImages);
    }

    /** Does PCA transform on the last manipulation recorded in the database. */
    private void conductPcaHelper(SessionFactory sessionFactory,
            List<ManipulatedImage> manipulatedImages) {
        File[] imageFiles = getManipulatedImagesFiles(manipulatedImages);
        PcaTransformer pcaTransformer = new PcaTransformer(ImageScaler.TARGET_HEIGHT,
                ImageScaler.TARGET_WIDTH, imageFiles);
        double [] averageFacePixels = pcaTransformer.getAverageFace();

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Transformation transformation = new Transformation();
        transformation.setAverageFacePixels(averageFacePixels);
        transformationIndex = (Integer) session.save(transformation);
        session.getTransaction().commit();

        // Outputting the average image for debugging purposes.
        String averageImageFilePath = String.format(AVERAGE_FACE_FILE_PATTERN, transformationIndex);
        ImageWriter.createImage(averageImageFilePath, averageFacePixels, ImageScaler.TARGET_HEIGHT,
                ImageScaler.TARGET_WIDTH, false);

        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        List<EigenFace> eigenFaces = pcaTransformer.getEigenFaces();
        EigenFaceEntity [] eigenFaceEntities = new EigenFaceEntity[eigenFaces.size()];
        for (int i = 0; i < eigenFaces.size(); i++) {
            eigenFaceEntities[i] = new EigenFaceEntity();
            eigenFaceEntities[i].setEigenValue(eigenFaces.get(i).eigenValue);
            eigenFaceEntities[i].setFacePixels(eigenFaces.get(i).eigenFacePixels);
            eigenFaceEntities[i].setTransformation(transformation);

            session.save(eigenFaceEntities[i]);
            printEigenFace(eigenFaceEntities[i], i);
        }

        for (ManipulatedImage manipulatedImage : manipulatedImages) {
            ColorPixel [][] imagePixels =
                    ImageReader.getImagePixels(new File(manipulatedImage.getManipulatedImagePath()));
            double[] pcaCoeficients =
                    pcaTransformer.getPcaCoeficients(GrayscaleConverter.getImageGrayscale(imagePixels));
            for (int i = 0; i < eigenFaceEntities.length; i++) {
                PcaCoeficient pcaCoeficient = new PcaCoeficient();
                pcaCoeficient.setManipulatedImage(manipulatedImage);
                pcaCoeficient.setEigenFace(eigenFaceEntities[i]);
                pcaCoeficient.setCoeficient(pcaCoeficients[i]);
                session.save(pcaCoeficient);
            }

            TransformedImage transformedImage = new TransformedImage();
            transformedImage.setManipulatedImage(manipulatedImage);
            transformedImage.setTransformation(transformation);
            session.save(transformedImage);
        }
        session.getTransaction().commit();
    }

    /** Constructs an array containing all the images associated with the last successful manipulation. */
    private List<ManipulatedImage> getLastManipulationImages(SessionFactory sessionFactory) {
        Integer lastManipulationIndex = DatabaseHelper.getLastManipulationIndex(sessionFactory);
        return DatabaseHelper.getGoodManipulationImages(sessionFactory, lastManipulationIndex);
    }

    /** Returns an array holding all the files of the manipulated images in the given array. */
    private File [] getManipulatedImagesFiles(List<ManipulatedImage> manipulatedImages) {
        File [] imageFiles = new File[manipulatedImages.size()];
        for (int i = 0; i < manipulatedImages.size(); i++) {
            imageFiles[i] = new File(manipulatedImages.get(i).getManipulatedImagePath());
        }
        return imageFiles;
    }

    /**
     * A method that stores the given eigen face in the database.
     *
     * The faces are also recorded in the file system but the images are just approximations,
     * because some of the pixel values can not be displayed properly in an image.
     */
    private void printEigenFace(EigenFaceEntity eigenFaceEntity, int idx) {
        ImageWriter.createImage(constructFaceFilePath(idx),
                eigenFaceEntity.normalizeForPrinting(), ImageScaler.TARGET_HEIGHT,
                ImageScaler.TARGET_WIDTH, false);
    }

    /** Constructs the path in which the face file will be created. */
    private String constructFaceFilePath(int idx) {
        return String.format(EGIEN_FACE_FILE_PATTERN, transformationIndex, idx);
    }
}
