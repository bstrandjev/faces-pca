package com.borisp.faces.util;


/** A class providing methods to convert color pixel to/from grayscale. */
public class GrayscaleConverter {
    public static int getGrayscaleValue(ColorPixel colorPixel) {
        int grayscale = 0;
        grayscale += 30 * colorPixel.red;
        grayscale += 59 * colorPixel.green;
        grayscale += 11 * colorPixel.blue;
        grayscale /= 100;
        return grayscale;
    }

    public static ColorPixel getColorPixel(int grayscale) {
        return new ColorPixel(grayscale, grayscale, grayscale);
    }

    /** Converts the color pixels of an image to grayscale. */
    public static int [][] getImageGrayscale(ColorPixel [][] imagePixels) {
        int [][] grayscale = new int[imagePixels.length][imagePixels[0].length];
        for (int i = 0; i < imagePixels.length; i++) {
            for (int j = 0; j < imagePixels[i].length; j++) {
                grayscale[i][j] = getGrayscaleValue(imagePixels[i][j]);
            }
        }
        return grayscale;
    }
}
