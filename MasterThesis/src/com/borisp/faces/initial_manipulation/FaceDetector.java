package com.borisp.faces.initial_manipulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.borisp.faces.initial_manipulation.ImageCropper.CropRegion;
import com.borisp.faces.util.ImageWriter;

/**
 * Finds the face region of given person's portrait picture.
 *
 * The used algorithm is as follows (main function {@link #findFace(int[][], boolean, boolean)}):
 * <ol>
 *  <li>Combined horizontal and vertical contour detection is performed using
 *      {@link #horizontalFilter(int[][])} and {@link #verticalFilter(int[][])}
 *  <li>The resulting pixel values are fit in the range [0, 255] in {@link #findFace(int[][], boolean, boolean)}
 *  <li>A linear smoothing filter with small rectangular shape is applied in
 *      {@link Smoother#smoothItFirst(int[][])}
 *  <li>The horizontal characteristic of the obtained picture is calculated in
 *      {@link #aggregatorHorizontal(int[][], int[][])}
 *  <li>The eye line is found in {@link #findEyes(int[][])}, searching the brightest region
 *      with regards to the characteristic
 *  <li>The vertical characteristic is calculated only for regions horizontally close to the already
 *      found eye line using {@link #aggregatorVertical(int[][], int, int)}.
 *  <li>The nose vertical is fond based on this characteristic as first step in
 *      {@link #findEyeXs(int[][])}
 *  <li>The x coordinates of the eye bounds are found as the bounds of the brightest regions around
 *      the nose in {@link #findEyeXs(int[][])}
 *  <li>The face bounding box is estimated based on heuristic constants in {@link #findFace(int[][], boolean, boolean)}
 *  <li>The face bounding box is rescaled to match the expected ration in
 *      {@link ImageScaler#chooseMostAppropriateRegion(CropRegion, int)}
 * </ol>
 *
 * @author Boris
 */
public class FaceDetector {
    private static String DEBUG_IMAGE_OUTPUT_DIRECTORY = "tmp/manipulation/";
    private static final String TIME_MEASURE_OUTPUT_PATTERN = "Operation '%s' took %.2f seconds";

    private static int margin = 10;

    /** Used for time profiling of the algorithms. */
    private static long lastRecordedTime;

    /**
     * Returns the region with the face in the image.
     *
     * @param grayscale The grayscale pixels of the image to analyze
     * @param doDebugOutput If set to true images visualizing the different phases of the face
     *        detection will be output in the process of detection.
     * @param measureTime A flag telling whether the time the different operations are taking should
     *        be logged
     * @return The region in which the face on the picture is believed to be.
     */
    public static CropRegion findFace(int[][] grayscale, boolean doDebugOutput, boolean measureTime) {
        int[][] debugGrayscale = null; // will be used only if doDebugOutput is true.

        if (doDebugOutput) {
            doDebugOutput("grayscale.jpg", grayscale);
        }

        if (measureTime) {
            lastRecordedTime = System.currentTimeMillis();
        }

        int[][] horiz = horizontalFilter(grayscale);
        int[][] vertical = verticalFilter(grayscale);
        int h = grayscale.length;
        int w = grayscale[0].length;
        margin = Math.max(10, Math.min(h / 60, w / 60));
        int[][] res = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                res[i][j] = (int) Math.sqrt((horiz[i][j] * horiz[i][j] + vertical[i][j]
                        * vertical[i][j]));
            }
        }
        if (doDebugOutput) {
            doDebugOutput("before_smoothing.jpg", res);
        }

        if(measureTime) {
            printTimeTaken("Linear filters");
        }

        int[][] res2 = new Smoother(res).smoothIt();
        if (doDebugOutput) {
            doDebugOutput("after_smoothing.jpg", res2);
        }
        if(measureTime) {
            printTimeTaken("Smoothing");
        }

        int[][] aggHoriz = aggregatorHorizontal(res2, new int[][] { { 0, w - 1 } });
        int eyes = findEyes(aggHoriz);
        if (doDebugOutput) {
            debugGrayscale = Arrays.copyOf(aggHoriz, aggHoriz.length);
            for (int i = 0; i < res2[0].length; i++) {
                debugGrayscale[eyes - 1][i] = 255;
                debugGrayscale[eyes][i] = 255;
                debugGrayscale[eyes + 1][i] = 255;
            }
            doDebugOutput("eye_line.jpg", debugGrayscale);
        }

        if(measureTime) {
            printTimeTaken("Find eyes horizonatal");
        }

        int[][] aggVertical = aggregatorVertical(res2, eyes - h / 20, eyes + h / 20);
        Integer[] eyeXs = null;
        try {
            eyeXs = findEyeXs(aggVertical);

            if (doDebugOutput) {
                for (int i = 0; i < debugGrayscale.length; i++) {
                    if (i > margin && i < debugGrayscale.length - margin) {
                        continue;
                    }
                    for (int j = 0; j < debugGrayscale[0].length; j++) {
                        debugGrayscale[i][j] = aggVertical[i][j];
                    }
                }
                for (Integer i : eyeXs) {
                    for (int j = 0; j < debugGrayscale.length; j++) {
                        debugGrayscale[j][i] = 255;
                        debugGrayscale[j][i - 1] = 255;
                        debugGrayscale[j][i + 1] = 255;
                    }
                }
                doDebugOutput("eye_boxes.jpg", debugGrayscale);
            }

            if(measureTime) {
                printTimeTaken("Find eye boxes");
            }

            aggHoriz = aggregatorHorizontal(res2, new int[][] { { eyeXs[2], eyeXs[0] },
                    { eyeXs[1], eyeXs[3] } });
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (j <= margin || (w - j) <= margin) {
                        res[i][j] = aggHoriz[i][j];
                    } else if (i <= margin || (h - i) <= margin) {
                        res[i][j] = aggVertical[i][j];
                    }
                }
            }
            // int top = findTopOfHead(aggHoriz, margin / 2);
            // top = topHelper(aggHoriz, top + 1, aggHoriz[top][0] * 2);
            int difference = eyeXs[2] - eyeXs[3];
            int before = eyeXs[1] - difference / 2;
            int after = eyeXs[0] + difference / 2;
            int horizDiff = (9 * (eyeXs[0] - eyeXs[1])) / 10;
            int beforeHoriz = eyes - horizDiff;
            int afterHoriz = eyes + (horizDiff * 3) / 2;
            for (int i = before; i < after; i++) {
                res[beforeHoriz][i] = 255;
                res[afterHoriz][i] = 255;
                res[eyes][i] = 255;
            }
            for (int i = beforeHoriz; i < afterHoriz; i++) {
                res[i][before] = 255;
                res[i][after] = 255;
            }

            if (doDebugOutput) {
                doDebugOutput("detected_face.jpg", res);
            }

            if(measureTime) {
                printTimeTaken("Detect face");
            }

            int faceWidth = after - before;
            int faceHeight = afterHoriz - beforeHoriz;
            int upFaceHeight = eyes - beforeHoriz;
            CropRegion region = new CropRegion();
            region.x1 = Math.max(margin + 1, beforeHoriz - (2 * upFaceHeight) / 3);
            region.y1 = Math.max(margin + 1, before - (3 * faceWidth) / 7);
            region.x2 = Math.min(h - margin - 1, afterHoriz + (3 * faceHeight) / 7);
            region.y2 = Math.min(w - margin - 1, after + (3 * faceWidth) / 7);

            if (doDebugOutput) {
                debugGrayscale = Arrays.copyOf(res, res.length);
                for (int i = region.x1; i < region.x2; i++) {
                    debugGrayscale[i][region.y1] = 255;
                    debugGrayscale[i][region.y2] = 255;
                }
                for (int i = region.y1; i < region.y2; i++) {
                    debugGrayscale[region.x1][i] = 255;
                    debugGrayscale[region.x2][i] = 255;
                }
                doDebugOutput("face_region.jpg", debugGrayscale);
            }

            return ImageScaler.chooseMostAppropriateRegion(region, w * h);
        } catch (RuntimeException e) {
            CropRegion cropRegion = new CropRegion();
            cropRegion.x1 = 0;
            cropRegion.y1 = 0;
            cropRegion.x2 = 240;
            cropRegion.y2 = 320;
            System.err.println("Failed to find the face");
            return cropRegion;
        }
    }

    /** A method that does the debug output of the intermediate images. */
    private static void doDebugOutput(String imageName, int [][] grayscale) {
        ImageWriter.createImage(DEBUG_IMAGE_OUTPUT_DIRECTORY + imageName, grayscale, false);
    }

    private static void printTimeTaken(String operationName) {
        long currentTimeMillis = System.currentTimeMillis();
        double seconds = (currentTimeMillis - lastRecordedTime) / 1000.0;
        System.out.println(String.format(TIME_MEASURE_OUTPUT_PATTERN, operationName, seconds));
        lastRecordedTime = currentTimeMillis;
    }

    private static int findEyes(int [][] aggHorizontal) {
        int h = aggHorizontal.length;
        int bestVal = 0;
        int eyeLine = 0;
        int aboveSegment = h / 40;
        int belowSegment = h / 120;
        for (int i = Math.max(margin, h / 4); i < h / 2; i++) {
            int sum = 0;
            for (int j = -aboveSegment; j <= belowSegment; j++) {
                sum += aggHorizontal[i + j][0];
            }
            if (sum > bestVal) {
                bestVal = sum;
                eyeLine = i;
            }
        }
        return eyeLine;
    }

    @SuppressWarnings("unused")
    private static int findTopOfHead(int [][] aggHoriz, int start) {
        int h = aggHoriz.length;
        int initDiff = 0;
        int maxm = 0;
        for (int i = 2; i < margin; i++) {
            initDiff = Math.max(initDiff, Math.abs(aggHoriz[i][0] - aggHoriz[i - 1][0]));
            if (i < margin / 2) {
                maxm = Math.max(aggHoriz[i][0], maxm);
            }
        }
        maxm = Math.min(3 * maxm, 60);
        boolean stable = true;
        return topHelper(aggHoriz, start, maxm);
    }

    /** Function helper for find top of head. */
    private static int topHelper(int [][] aggHoriz, int start, int maxm) {
        int h = aggHoriz.length;
        for (int i = start; i < h - margin / 2; i++) {
            if (aggHoriz[i][0] - Math.min(aggHoriz[i - 1][0], aggHoriz[i - 2][0]) > 20
                    && aggHoriz[i][0] > maxm) {
                int result = i;
                int found = aggHoriz[i][0];
                for (int j = i + 1; j < h; j++) {
                    if (aggHoriz[j][0] < aggHoriz[j - 1][0]) {
                        return result;
                    } else if (aggHoriz[j][0] > found) {
                        found = aggHoriz[j][0];
                        result = j;
                    }
                }
                return i;
            }
        }
        return 0;
    }
    private static Integer[] findEyeXs(int [][] aggVertical) {
        List<Integer> toRet = new ArrayList<Integer>();
        int w = aggVertical[0].length;
        int mid = w / 2;
        int c = w / 2;
        for (int i = -20; i < 20; i++) {
            if (aggVertical[0][c + i] < aggVertical[0][mid]) {
                mid = c + i;
            }
        }
        int [] step = {1, -1};
        int [] aimedForValue = new int[2];
        for (int k = 0; k < 2; k++) {
            int beg = Math.max(aggVertical[0][mid], 20);
            boolean overTheTop = false;
            int best = 0;
            for (int i = mid; Math.abs(mid - i) < w / 4; i += step[k]) {
                best = Math.max(aggVertical[0][i], best);
                if (overTheTop && (aggVertical[0][i] - 10 < beg || aggVertical[0][i] < best / 2)) {
                    toRet.add(i);
                    aimedForValue[k] = aggVertical[0][i];
                    break;
                } else if (aggVertical[0][i] > beg * 2) {
                    overTheTop = true;
                }
            }
        }
        int center = (toRet.get(0) + toRet.get(1)) / 2;
        for (int k = 0; k < 2; k++) {
            for (int i = center; Math.abs(center - i) < w / 4; i += step[k]) {
                if (aggVertical[0][i] >= aimedForValue[k]) {
                    toRet.add(i);
                    break;
                }
            }
        }
        return toRet.toArray(new Integer[toRet.size()]);
    }

    public static int [][] horizontalFilter(int [][]grayscale) {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][]result = new int [h][w];
        for (int i = 0; i < w; i++) {
            result[0][i] = 0;
        }
        int maxm = 0;
        for (int i = 1; i < h; i++) {
            for (int j = 0; j < w; j++) {
                result[i][j] = Math.abs(grayscale[i][j] - grayscale[i - 1][j]);
                if (i > margin && h - i > margin)
                    maxm = Math.max(result[i][j], maxm);
            }
        }
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                result[i][j] *= 255 / maxm;
                result[i][j] = Math.min(255, result[i][j]);
                result[i][j] = Math.max(0, result[i][j]);
            }
        }
        return result;
    }

    public static int [][] aggregatorHorizontal(int [][]grayscale, int [][] intervals) {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][]result = new int [h][w];
        int []rows = new int [h];
        int maxm = 0;
        int minm = 10000000;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < intervals.length; j++) {
                for (int k = intervals[j][0]; k <= intervals[j][1]; k++) {
                    rows[i] += grayscale[i][k];
                }
            }
        }
        rows = smoother(rows);
        //rows = smoother2(rows);
        for (int i = 0; i < h / 2; i++) {
            if (i > margin && h - i > margin) {
                maxm = Math.max(maxm, rows[i]);
                minm = Math.min(minm, rows[i]);
            }
        }
        for (int i = 0; i < h; i++) {
            int value = ((rows[i] - minm) * 255) / (maxm - minm);
            if (rows[i] < minm) {
                value = 0;
            } else if (rows[i] > maxm) {
                value = 255;
            }
            for (int j = 0; j < w; j++) {
                if ( j > margin && w - j > margin) {
                    result[i][j] = grayscale[i][j];
                    continue;
                }
                result[i][j] = value;
            }
        }
        return result;
    }

    public static int [][] verticalFilter(int [][]grayscale) {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][]result = new int [h][w];
        for (int i = 0; i < h; i++) {
            result[i][0] = 0;
        }
        int maxm = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 1; j < w; j++) {
                result[i][j] = Math.abs(grayscale[i][j] - grayscale[i][j - 1]);
                maxm = Math.max(result[i][j], maxm);
            }
        }
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                result[i][j] *= 255 / maxm;
            }
        }
        return result;
    }

    public static int [][] aggregatorVertical(int [][]grayscale, int beg, int end) {
        int h = grayscale.length;
        int w = grayscale[0].length;
        int [][]result = new int [h][w];
        int []cols = new int [w];
        int maxm = 0;
        int minm = 10000000;
        for (int i = margin; i <= w - margin; i++) {
            for (int j = beg; j <= end; j++) {
                cols[i] += grayscale[j][i];
            }
        }
        cols = smoother(cols);
        for(int i = w / 3; i <= (2 * w) / 3; i++) {
            maxm = Math.max(maxm, cols[i]);
            minm = Math.min(minm, cols[i]);
        }
        for (int i = 0; i < w; i++) {
            int value = ((cols[i] - minm)* 255) / (maxm - minm);
            value = Math.min(value,  255);
            value = Math.max(value,  0);
            for (int j = 0; j < h; j++) {
                if (j > margin && h - j > margin) {
                    result[j][i] = grayscale[j][i];
                    continue;
                }
                result[j][i] = value;
            }
        }
        return result;
    }

    private static int [] smoother(int [] a) {
        int [] res = new int [a.length];
        res[0] = (a[0] * 3 + a[1] * 2) / 5;
        res[res.length - 1] = (a[res.length - 1] * 3 + a[res.length - 2] * 2) / 5;
        for (int i = 1; i < res.length - 1; i++) {
            res[i] = (a[i - 1] + a[i + 1]  + 2 * a[i]) / 4;
        }
        return res;
    }
}
