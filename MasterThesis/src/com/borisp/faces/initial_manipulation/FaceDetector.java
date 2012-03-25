package com.borisp.faces.initial_manipulation;

import java.util.ArrayList;
import java.util.List;

import com.borisp.faces.initial_manipulation.ImageCropper.CropRegion;

public class FaceDetector {
    private static int margin = 10;

    /** Returns the region with the face in the image. */
    public static CropRegion findFace(int [][] grayscale) {
        int[][] horiz = horizontalFilter(grayscale);
        int[][] vertical = verticalFilter(grayscale);
        int h = grayscale.length;
        int w = grayscale[0].length;
        margin = Math.max(10, Math.min(h / 60, w / 60));
        int[][] res = new int [h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                res[i][j] = (int)Math.sqrt((horiz[i][j] * horiz[i][j] + vertical[i][j] * vertical[i][j]));
            }
        }
        int[][] res2 = smoothItFirst(res);
        int[][] aggHoriz = aggregatorHorizontal(res2, new int [][]{{0, w - 1}});
        int eyes = findEyes(aggHoriz);
        int[][] aggVertical  = aggregatorVertical(res2, eyes - h / 20, eyes + h / 20);
        Integer [] eyeXs = findEyeXs(aggVertical);
        aggHoriz = aggregatorHorizontal(res2, new int [][]{{eyeXs[2], eyeXs[0]}, {eyeXs[1], eyeXs[3]}});
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (j <= margin || (w - j) <= margin) {
                    res[i][j] = aggHoriz[i][j];
                } else if (i <= margin || (h - i) <= margin) {
                    res[i][j] = aggVertical[i][j];
                }
            }
        }
        //int top = findTopOfHead(aggHoriz, margin / 2);
        //top = topHelper(aggHoriz, top + 1, aggHoriz[top][0] * 2);
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

        int faceWidth = after - before;
        int faceHeight = afterHoriz - beforeHoriz;
        int upFaceHeight = eyes - beforeHoriz;
        CropRegion region = new CropRegion();
        region.x1 = Math.max(margin + 1, beforeHoriz - (2 * upFaceHeight) / 3);
        region.y1 = Math.max(margin + 1, before - (3 * faceWidth) / 7);
        region.x2 = Math.min(h - margin - 1, afterHoriz + (3 * faceHeight) / 7);
        region.y2 = Math.min(w - margin - 1, after + (3 * faceWidth) / 7);

        return ImageScaler.chooseMostAppropriateRegion(region, w * h);
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

    private static int [][]smoothItFirst(int [][] grayscale) {
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
