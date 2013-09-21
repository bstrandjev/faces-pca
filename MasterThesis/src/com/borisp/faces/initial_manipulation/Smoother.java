package com.borisp.faces.initial_manipulation;

/**
 * Class providing method for smoothing filter
 *
 * @author Boris Strandjev
 */
public class Smoother {

    private int[][] grayscale;

    public Smoother(int [][] grayscale) {
        this.grayscale = grayscale;
    }

    /** Applies linear smoothing filter aggregating the values in rectangular region. */
    public int [][] smoothIt() {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][] aggregated = new int[h][w];
        int [][] res = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                aggregated[i][j] = grayscale[i][j];
                if (i > 0) {
                    aggregated[i][j] += aggregated[i - 1][j];
                }
                if (j > 0) {
                    aggregated[i][j] += aggregated[i][j - 1];
                }
                if (j > 0 && i > 0) {
                    aggregated[i][j] -= aggregated[i - 1][j - 1];
                }
            }
        }

        int sideHoriz = h / 120;
        int sideVertical = w / 120;
        int x1, y1, x2, y2;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                x1 = Math.max(0, i - sideHoriz);
                y1 = Math.max(0, j - sideVertical);
                x2 = Math.min(h - 1, i + sideHoriz);
                y2 = Math.min(w - 1, j + sideVertical);
                res[i][j] = aggregated[x2][y2];
                if (x1 > 0) {
                    res[i][j] -= aggregated[x1 - 1][y2];
                }
                if (y1 > 0) {
                    res[i][j] -= aggregated[x2][y1 - 1];
                }
                if (y1 > 0 && x1 > 0) {
                    res[i][j] += aggregated[x1 - 1][y1 - 1];
                }
                res[i][j] /= (x2 - x1 + 1) * (y2 - y1 + 1);
            }
        }
        return res;
    }
}
