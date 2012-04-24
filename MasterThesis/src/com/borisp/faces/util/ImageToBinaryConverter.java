package com.borisp.faces.util;

import java.awt.image.BufferedImage;

/**
 * Class defining methods to convert images to / from binary array.
 *
 * @author Boris
 */
public class ImageToBinaryConverter {
    public static BufferedImage getBufferImage(byte [] imageBytes, int height, int width) {
        if (imageBytes == null) {
            return null;
        }

        int [] pixels = new int[imageBytes.length / 4];
        for (int i = 0; i < imageBytes.length; i++) {
            pixels[i / 4] |= (imageBytes[i] <<  (3 - i % 4));
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    public static byte [] getImageBytes(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] rgbArray = new int [h * w];
        img.getRGB(0, 0, w, h, rgbArray, 0, w);
        byte [] bytes = new byte[rgbArray.length * 4];
        for (int i = 0; i < rgbArray.length; i++) {
            bytes[i * 4] = (byte)((rgbArray[i] >>> 3) & 0xFF);
            bytes[i * 4 + 1] = (byte)((rgbArray[i] >>> 2) & 0xFF);
            bytes[i * 4 + 2] = (byte)((rgbArray[i] >>> 1) & 0xFF);
            bytes[i * 4 + 3] = (byte)(rgbArray[i] & 0xFF);
        }
        return bytes;
    }
}
