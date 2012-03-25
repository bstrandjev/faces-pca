package com.borisp.faces.initial_manipulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.ImageWriter;
import com.borisp.faces.util.Pixel;

public class BackgroundExtractor {
    private static int [][] neighPixels = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private static int [][] nearNeighPixels = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private static int BACKGROUND_PIXEL_VALUE = 128;

    // Parameters of the image
    private int h, w;
    private ColorPixel [][] pixels;

    public BackgroundExtractor(ColorPixel [][] greyscale) {
        this.h = greyscale.length;
        this.w = greyscale[0].length;
        this.pixels = greyscale.clone();
    }

    public void extractBackground(String fileName) {
        List<Pixel> background = findInitialBackground();
        Integer [][] grayscale = new Integer[h][w];
        for (Pixel pixel : background) {
            grayscale[pixel.x][pixel.y] = BACKGROUND_PIXEL_VALUE;
        }
        cleanImage(grayscale);
        ImageWriter.createImage(fileName, grayscale, true);
    }

    private void cleanImage(Integer [][] greyscale) {
        boolean [][] vis = new boolean[h][w];
        Queue<Pixel> toProcess = new LinkedList<Pixel>();
        toProcess.add(new Pixel(h - 1,  w / 2));
        vis[h - 1][w / 2] = true;
        Pixel cur;
        int neighX, neighY;
        while (!toProcess.isEmpty()) {
            cur = toProcess.poll();
            for (int i = 0; i < nearNeighPixels.length; i++) {
                neighX = cur.x + nearNeighPixels[i][0];
                neighY = cur.y + nearNeighPixels[i][1];
                if (isInImage(neighX, neighY) && !vis[neighX][neighY]
                        && greyscale[neighX][neighY] == 0) {
                    toProcess.add(new Pixel(neighX, neighY));
                    vis[neighX][neighY] = true;
                }
            }
        }
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (!vis[i][j])
                    greyscale[i][j] = BACKGROUND_PIXEL_VALUE;
            }
        }
    }

    private List<Pixel> findInitialBackground() {
        List<Pixel> background = new ArrayList<>();
        Queue<Pixel> toProcess = new LinkedList<Pixel>();
        boolean [][] vis = new boolean[h][w];
        for (int i = 0; i < 20; i++){
            for (int j = 0; j < 20; j++) {
                toProcess.add(new Pixel(i, j));
                vis[i][j] = true;
                toProcess.add(new Pixel(i, w - j - 1));
                vis[i][w - j - 1] = true;
            }
        }
        Pixel cur;
        int neighX, neighY;
        while (!toProcess.isEmpty()) {
            cur = toProcess.poll();
            background.add(cur);
            for (int i = 0; i < neighPixels.length; i++) {
                neighX = cur.x + neighPixels[i][0];
                neighY = cur.y + neighPixels[i][1];
                if (isInImage(neighX, neighY) && !vis[neighX][neighY]
                        && isBorder(neighX, neighY, pixels[cur.x][cur.y])) {
                    toProcess.add(new Pixel(neighX, neighY));
                    vis[neighX][neighY] = true;
                }
            }
        }
        return background;
    }

    private boolean isBorder(int x, int y, ColorPixel neighbourColor) {
        if (colorDiff(neighbourColor, pixels[x][y]) > 18) {
            return false;
        }
        int neighX, neighY;
        int cnt = 0;
        int out = 0;
        for (int i = 0; i < neighPixels.length; i++) {
            neighX = x + neighPixels[i][0];
            neighY = y + neighPixels[i][1];
            if (isInImage(neighX, neighY)) {
                if (colorDiff(pixels[x][y], pixels[neighX][neighY]) <= 18) {
                    cnt++;
                }
            } else {
                out++;
            }
        }
        return cnt + out / 3 > 4;
    }

    private int colorDiff(ColorPixel left, ColorPixel right) {
        return Math.abs(left.red - right.red) * Math.abs(left.red - right.red)
                + Math.abs(left.blue - right.blue) * Math.abs(left.blue - right.blue)
                + Math.abs(left.green - right.green) * Math.abs(left.green - right.green);
    }

    private boolean isInImage(int x, int y) {
        return x >= 0 && y >= 0 && x < h && y < w;
    }
}
