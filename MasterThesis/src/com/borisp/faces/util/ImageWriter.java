package com.borisp.faces.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.borisp.faces.initial_manipulation.ImageScaler;

public class ImageWriter {
    private static final String IMAGE_OUT_DIR = "out_images";
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
        int []imagePixels = new int[grayScale.length];
        for (int i  = 0; i < grayScale.length; i++) {
            imagePixels[i] = ((1 << 16) | (1 << 8) | 1) * (int)grayScale[i];
        }
        writeImage(getImageFromArray(imagePixels, w, h), fileName, needScaling);
    }

    /**
     * Creates an image stored in file from two-dimensional grayscale array.
     *
     * @param fileName The name of the file in which the image will be stored (only name, not path).
     * @param grayScale The grayscale image pixels.
     * @param needScaling whether the image should be scaled to fit prespecified dimensions
     */
    public static void createImage(String fileName, Number [][] grayScale, boolean needScaling) {
        int w = grayScale[0].length;
        int h = grayScale.length;
        int []imagePixels = new int[h * w];
        for (int i  = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                imagePixels[i * w + j] = ((1 << 16) | (1 << 8) | 1) * grayScale[i][j].intValue();
            }
        }
        writeImage(getImageFromArray(imagePixels, w, h), fileName, needScaling);
    }

    /** Prints an image to a file stored in the default image location: {@link #IMAGE_OUT_DIR}. */
    public static void createImage(String fileName, ColorPixel [][] colors, boolean needScaling) {
        writeImage(loadColorPixelsToImage(colors), fileName, needScaling);
    }

    /** Prints an image to the specified location. */
    public static void createImage(File outputFile, ColorPixel [][] colors, boolean needScaling) {
        writeImage(loadColorPixelsToImage(colors), outputFile, needScaling);
    }

    public static BufferedImage loadColorPixelsToImage(ColorPixel [][] colors) {
        int w = colors[0].length;
        int h = colors.length;
        int []imagePixels = new int[h * w];
        for (int i  = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                imagePixels[i * w + j] =
                        (colors[i][j].red << 16) | (colors[i][j].green << 8) | colors[i][j].blue;
            }
        }
        return getImageFromArray(imagePixels, w, h);
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

    private static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }
}
