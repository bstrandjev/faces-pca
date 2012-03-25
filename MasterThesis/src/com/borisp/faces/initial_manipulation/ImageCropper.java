package com.borisp.faces.initial_manipulation;

import com.borisp.faces.util.ColorPixel;

public class ImageCropper {
    public static class CropRegion {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
    }

    public ColorPixel[][] cropImage(ColorPixel[][] imagePixels, CropRegion region) {
        ColorPixel [][]res = new ColorPixel [region.x2 - region.x1 + 1][region.y2 - region.y1 + 1];
        for (int i = region.x1; i <= region.x2; i++) {
            for (int j = region.y1; j <= region.y2; j++) {
                res[i - region.x1][j - region.y1] = imagePixels[i][j];
            }
        }
        return res;
    }
}
