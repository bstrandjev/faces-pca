package com.borisp.faces.pca.test;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.borisp.faces.initial_manipulation.Smoother;

public class SmootherTest {
    private static final long SEED = 23324;

    private Random random;

    @Before
    public void setup() {
        random = new Random(SEED);
    }

    /** This is the old sub-optimal smooth it first implementation. */
    private int [][]smoothIt(int [][] grayscale) {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][] res = new int[h][w];
        int sideHoriz = w / 120;
        int sideVertical = h / 120;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int cnt = 0;
                for (int k = -sideVertical; k <= sideVertical; k++) {
                    for (int l = -sideHoriz; l <= sideHoriz; l++) {
                        if (i + k >= 0 && i + k < h && j + l >= 0 && j + l < w) {
                            res[i][j] += grayscale[i + k][j + l];
                            cnt++;
                        }
                    }
                }
                res[i][j] /= cnt;
                if (res[i][j] > 255) {
                    res[i][j] = 255;
                }
            }
        }
        return res;
    }

    private int generateRandomPixelValue() {
        return random.nextInt(256);
    }

    private int [][] generateRandomArray(int h, int w) {
        int [][] grayscales = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                grayscales[i][j] = generateRandomPixelValue();
            }
        }
        return grayscales;
    }

    /** Compares the contents of two arrays. */
    private void assertArraysEqual(int [][] array1, int [][] array2) {
        assertEquals("Expected same array size", array1.length, array2.length);
        for (int i = 0; i < array1.length; i++) {
            assertEquals("Expected same array size", array1[i].length, array2[i].length);
            for (int j = 0; j < array1[i].length; j++) {
                assertEquals("Expected same array contents", array1[i][j], array2[i][j]);
            }
        }
    }

    /**
     * Tests that the optimization of the smooth it first works as expected.
     */
    @Test
    public void testSmoothIt() {
        int[][] array = generateRandomArray(300, 450);
        int[][] smootherResult = new Smoother(array).smoothIt();
        assertArraysEqual(smoothIt(array), smootherResult);

        array = generateRandomArray(1000, 834);
        smootherResult = new Smoother(array).smoothIt();
        assertArraysEqual(smoothIt(array), smootherResult);

        array = generateRandomArray(263, 341);
        smootherResult = new Smoother(array).smoothIt();
        assertArraysEqual(smoothIt(array), smootherResult);

        array = generateRandomArray(407, 905);
        smootherResult = new Smoother(array).smoothIt();
        assertArraysEqual(smoothIt(array), smootherResult);

        array = generateRandomArray(2400, 2400);
        smootherResult = new Smoother(array).smoothIt();
        assertArraysEqual(smoothIt(array), smootherResult);
    }
}
