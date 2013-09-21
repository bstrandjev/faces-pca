package com.borisp.faces.initial_manipulation;

import java.io.File;

import com.borisp.faces.initial_manipulation.ImageCropper.CropRegion;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;


public class ImageManipulator {
    /** The directory which stores the sample images. */
    private static final String IMAGES_DIRECTORY = "images" + File.separator + "initial";

    public void manipulateImages() {
        File imageDirectory = new File(IMAGES_DIRECTORY);
        for (String imageFileName : imageDirectory.list()) {
            String imageFilePath = imageDirectory.getPath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (imageFile.isDirectory()) {
                continue;
            }
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
            int[][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            CropRegion faceRegion = FaceDetector.findFace(grayscale, true, false);
            ImageCropper cropper = new ImageCropper();
            ImageWriter.createImage(imageFileName, cropper.cropImage(imagePixels, faceRegion), true);
        }
    }
}
