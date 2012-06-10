package com.borisp.faces.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.borisp.faces.initial_manipulation.ImageScaler;

public class ImageWriter {
    private static final String OUT_IMAGE_FORMAT = "jpg";

    /**
     * Creates an image stored in file from two-dimensional grayscale array.
     *
     * @param fileName The name of the file in which the image will be stored (only name, not path).
     * @param grayScale The grayscale image pixels in one-dimensional array.
     * @param h The height of the image in pixels
     * @param w The width of the image in pixels
     * @param needScaling whether the image should be scaled to fit prespecified dimensions
     */
    public static void createImage(String fileName, double[] grayScale, int h, int w,
            boolean needScaling) {
        writeImage(ImageConstructor.createImage(grayScale, h, w), fileName, needScaling);
    }

    /**
     * Creates an image stored in file from two-dimensional grayscale array.
     *
     * @param fileName The name of the file in which the image will be stored (only name, not path).
     * @param grayScale The grayscale image pixels.
     * @param needScaling whether the image should be scaled to fit prespecified dimensions
     */
    public static void createImage(String fileName, Number [][] grayScale, boolean needScaling) {
        writeImage(ImageConstructor.createImage(grayScale), fileName, needScaling);
    }

    /**
     * Creates an image stored in file from two-dimensional grayscale array.
     *
     * @param fileName The name of the file in which the image will be stored (only name, not path).
     * @param grayScale The grayscale image pixels.
     * @param needScaling whether the image should be scaled to fit prespecified dimensions
     */
    public static void createImage(String fileName, int [][] grayScale, boolean needScaling) {
        writeImage(ImageConstructor.createImage(grayScale), fileName, needScaling);
    }

    /** Prints an image to a file stored in the default image location: {@link #IMAGE_OUT_DIR}. */
    public static void createImage(String fileName, ColorPixel [][] colors, boolean needScaling) {
        writeImage(ImageConstructor.createImage(colors), fileName, needScaling);
    }

    /** Prints an image to the specified location. */
    public static void createImage(File outputFile, ColorPixel [][] colors, boolean needScaling) {
        writeImage(ImageConstructor.createImage(colors), outputFile, needScaling);
    }

    public static void writeImage(BufferedImage image, String fileName, boolean needScaling) {
        File outputFile = new File(fileName);
        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        writeImage(image, outputFile, needScaling);
    }

    private static void writeImage(BufferedImage image, File outputFile, boolean needScaling) {
        try {
            BufferedImage scaledImage = (needScaling) ? ImageScaler.rescaleImage(image) : image;
            ImageIO.write(scaledImage, OUT_IMAGE_FORMAT, outputFile);
        } catch (IOException e) {
            System.out.println("The image could not be written");
            e.printStackTrace();
        }
    }
}
