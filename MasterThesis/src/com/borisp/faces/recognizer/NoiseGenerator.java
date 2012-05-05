package com.borisp.faces.recognizer;

import java.io.File;
import java.util.Random;

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;

/**
 * A class that can be used to get an instance of the image with noise introduced.
 *
 * @author Boris
 */
public class NoiseGenerator {
    /** The exclusive upper bound of the grayscale value of a pixel. */
    private static final int GRAYSCALE_UPPER_BOUND = 256;

    /**
     * Returns an instance of the image's grayscale with noise introduced.
     * @param image the manipulated image to be used as input
     * @param noise the level of noise to introduce
     * @return the grayscale of the image scrambled with noise
     */
    public static int[][] getImageWithNoise(ManipulatedImage image, double noise) {
        Random rand = new Random();
        File manipulatedImageFile = new File(image.getManipulatedImagePath());
        int[][] grayscales = GrayscaleConverter.getImageGrayscale(ImageReader
                .getImagePixels(manipulatedImageFile));
        for (int i = 0; i < grayscales.length; i++) {
            for (int j = 0; j < grayscales[i].length; j++) {
                if (rand.nextDouble() <= noise) {
                    grayscales[i][j] = rand.nextInt(GRAYSCALE_UPPER_BOUND);
                }
            }
        }
        return grayscales;
    }
}
