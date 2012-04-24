package com.borisp.faces.database;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;

import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.initial_manipulation.FaceDetector;
import com.borisp.faces.initial_manipulation.ImageCropper;
import com.borisp.faces.initial_manipulation.ImageCropper.CropRegion;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.ui.ManipulationEvaluatorUI;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;

/**
 * A class that can be used to create new manipulation.
 * <p>
 * All the initial images will be fetched, each will be manipulated and the user will be shown a UI
 * dialog to select if the application of the manipulation on the image was successful or not.
 *
 * @author Boris
 */
public class ManipulationCreator {
    private static final String IS_GOOD_FILE_PATTERN = "images/manipulation_%2d/good/%s";
    private static final String IS_BAD_FILE_PATTERN = "images/manipulation_%2d/bad/%s";

    private BufferedImage manipulatedImage;
    private Manipulation manipulation;
    private CropRegion faceRegion;
    private SessionFactory sessionFactory;

    /** A class that is supposed to implement 'iterator' over all initial images. */
    public class ManipulationIterator {
        private static final String SELECT_ALL_SQL_QUERY = "from Image image";

        private List<Image> initialImages;
        private int currentImageIdx;

        private ManipulationIterator(Session session ) {
            this.initialImages = new ArrayList<Image>();
            Query query = session.createQuery(SELECT_ALL_SQL_QUERY);
            for (Iterator<?> it = query.iterate(); it.hasNext();) {
                initialImages.add((Image) it.next());
            }
            List<ManipulatedImage> manipulatedImages = manipulation.getManipulatedImages();
            this.currentImageIdx = manipulatedImages != null ? manipulatedImages.size() : 0;
            this.currentImageIdx = 25;
        }

        public String getCurrentImageLabel() {
            return initialImages.get(currentImageIdx).getKey();
        }

        public BufferedImage getCurrentImage() {
            try {
                BufferedImage manipulatedImage =
                        getManipulation(initialImages.get(currentImageIdx));
                return manipulatedImage;
            } catch (Throwable t) {
                t.printStackTrace();
                faceRegion = new CropRegion();
                faceRegion.x1 = -1;
                faceRegion.y1 = -1;
                faceRegion.x2 = -1;
                faceRegion.y2 = -1;
                manipulatedImage = null;
                recordGoodState(false);
                if (moveIterator()) {
                    return getCurrentImage();
                } else {
                    return null;
                }
            }
        }

        public boolean moveIterator() {
            if (currentImageIdx + 1 < initialImages.size() && currentImageIdx < 26) {
                currentImageIdx++;
                return true;
            } else {
                return false;
            }
        }

        public void recordGoodState(boolean isGood) {
            recordManipulatedImage(initialImages.get(currentImageIdx), isGood);
        }

    }

    public void createNewManipulation(SessionFactory sessionFactory, boolean newManipulation) {
        this.sessionFactory = sessionFactory;
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Manipulation.class).setProjection(
                Projections.max("manipulationIndex"));
        Integer maxIndex = (Integer) criteria.uniqueResult();

        if (newManipulation) {
            manipulation = new Manipulation();
            manipulation.setCreated(new Timestamp(System.currentTimeMillis()));
            maxIndex = maxIndex != null ? maxIndex + 1 : 1;
            manipulation.setManipulationIndex(maxIndex);
            session.save(manipulation);
            session.getTransaction().commit();
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
        } else {
            Query query =
                    session.createQuery("from Manipulation m where m.manipulationIndex = :index");
            query.setInteger("index", maxIndex);
            manipulation = (Manipulation) query.uniqueResult();
        }

        new ManipulationEvaluatorUI(new ManipulationIterator(session));
    }

    private BufferedImage getManipulation(Image image) {
        String imageFilePath = image.getImagePath();
        File imageFile = new File(imageFilePath);
        ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
        int[][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
        faceRegion = FaceDetector.findFace(grayscale);

        ImageCropper cropper = new ImageCropper();
        ColorPixel[][] grayscalePixels = new ColorPixel[grayscale.length][grayscale[0].length];
        for (int i = 0; i < grayscale.length; i++) {
            for (int j = 0; j < grayscale[0].length; j++) {
                grayscalePixels[i][j] = GrayscaleConverter.getColorPixel(grayscale[i][j]);
            }
        }
        ColorPixel[][] croppedImage = cropper.cropImage(grayscalePixels, faceRegion);
        manipulatedImage =
                ImageScaler.rescaleImage(ImageWriter.loadColorPixelsToImage(croppedImage));
        return manipulatedImage;
    }

    private void recordManipulatedImage(Image image, boolean isGood) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        ManipulatedImage manipulatedImage = new ManipulatedImage();
        manipulatedImage.setBegX(faceRegion.x1);
        manipulatedImage.setBegY(faceRegion.y1);
        manipulatedImage.setEndX(faceRegion.x2);
        manipulatedImage.setEndY(faceRegion.y2);
        manipulatedImage.setIsGood((byte)(isGood ? 1 : 0));
        String manipulatedImagePath = constructImagePath(image, isGood);
        manipulatedImage.setManipulatedImagePath(manipulatedImagePath);

        manipulatedImage.setOriginalImage(image);
        manipulatedImage.setManipulation(manipulation);
        session.save(manipulatedImage);
        session.getTransaction().commit();
        if (manipulatedImage != null) {
            ImageWriter.writeImage(this.manipulatedImage, manipulatedImagePath, false);
        }
    }

    /** Returns the file path in which the image is to be stored. */
    private String constructImagePath(Image image, boolean isGood) {
        if (isGood) {
            return String.format(IS_GOOD_FILE_PATTERN, manipulation.getManipulationIndex(),
                    image.getKey());
        } else {
            return String.format(IS_BAD_FILE_PATTERN, manipulation.getManipulationIndex(),
                    image.getKey());
        }
    }
}
