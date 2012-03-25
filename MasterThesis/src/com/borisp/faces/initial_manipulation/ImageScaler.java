package com.borisp.faces.initial_manipulation;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.borisp.faces.initial_manipulation.ImageCropper.CropRegion;


public class ImageScaler {
    public static Integer TARGET_WIDTH = 110;
    public static Integer TARGET_HEIGHT = 150;

    public static CropRegion chooseMostAppropriateRegion(CropRegion region, int totalArea) {
        CropRegion result = new CropRegion();
        result.x1 = region.x1;
        result.y1 = region.y1;
        result.x2 = region.x2;
        result.y2 = region.y2;
        boolean shouldDownscale = (result.x2 - result.x1) * (result.y2 - result.y1) * 10 > totalArea * 6;
        int step = shouldDownscale ? 1 : -1;
        if (((result.x2 - result.x1) * TARGET_WIDTH > (result.y2 - result.y1) * TARGET_HEIGHT)
                == shouldDownscale) {
            while (((result.x2 - result.x1) * TARGET_WIDTH > (result.y2 - result.y1) * TARGET_HEIGHT)
                    == shouldDownscale) {
                if (shouldDownscale) {
                    result.x2 -= step;
                } else {
                    result.x1 += step;
                }
            }
        } else {
            boolean left = true;
            while (((result.x2 - result.x1) * TARGET_WIDTH < (result.y2 - result.y1) * TARGET_HEIGHT)
                    == shouldDownscale) {
                if (left) {
                    result.y1 += step;
                } else {
                    result.y2 -= step;;
                }
                left = !left;
            }
        }
        return result;
    }

    public static BufferedImage rescaleImage(BufferedImage image) {
        return ImageScaler.getScaledInstance(image, TARGET_WIDTH, TARGET_HEIGHT,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
    }

    /**
     * Convenience method that returns a scaled instance of the provided
     * {@code BufferedImage}.
     *
     * @param img
     *            the original image to be scaled
     * @param targetWidth
     *            the desired width of the scaled instance, in pixels
     * @param targetHeight
     *            the desired height of the scaled instance, in pixels
     * @param hint
     *            one of the rendering hints that corresponds to
     *            {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *            {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *            {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *            {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality
     *            if true, this method will use a multi-step scaling technique
     *            that provides higher quality than the usual one-step technique
     *            (only useful in downscaling cases, where {@code targetWidth}
     *            or {@code targetHeight} is smaller than the original
     *            dimensions, and generally only when the {@code BILINEAR} hint
     *            is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img, int targetWidth,
            int targetHeight, Object hint, boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
