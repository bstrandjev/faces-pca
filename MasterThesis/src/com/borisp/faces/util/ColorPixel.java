package com.borisp.faces.util;

/** A class representing one colored pixel. */
public class ColorPixel {
    public int red;
    public int green;
    public int blue;

    public ColorPixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ColorPixel(int pixelRgb) {
        red = (pixelRgb >> 16) & 0xFF;
        green = (pixelRgb >> 8) & 0xFF;
        blue = pixelRgb & 0xFF;
    }

    public int getPixelValue() {
        int rgb = red;
        rgb = (rgb << 8) + green;
        rgb = (rgb << 8) + blue;
        return rgb;
    }
}
