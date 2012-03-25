package com.borisp.faces.pca.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.borisp.faces.pca.PcaTransformer;
import com.borisp.faces.pca.PcaTransformer.EigenBean;
import com.borisp.faces.pca.PcaTransformer.EigenFace;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;


public class PcaTransformerTest {
    /** Constant used for the double comparisons. */
    private static final double EPSILON = 1e-6;

    private static final String FIXTURE_PICTURE_DIRECTORY = "fixtures";
    private static final int FIXTURE_PICTURE_HEIGHT = 30;
    private static final int FIXTURE_PICTURE_WIDTH = 22;

    private class PcaTransformerMock extends PcaTransformer {
        public PcaTransformerMock() {
            super(FIXTURE_PICTURE_HEIGHT, FIXTURE_PICTURE_WIDTH, FIXTURE_PICTURE_DIRECTORY);
        }

        public PcaTransformerMock(String fixtureDirectory) {
            super(FIXTURE_PICTURE_HEIGHT, FIXTURE_PICTURE_WIDTH, fixtureDirectory);
        }

        @Override
        protected List<EigenFace> getEigenFacesGrayscale() {
            return super.getEigenFacesGrayscale();
        }

        @Override
        protected double[][] getImageGrayscales() {
            return super.getImageGrayscales();
        }

        @Override
        protected EigenBean[] calculateEigens(double[][] inputMatrix) {
            return super.calculateEigens(inputMatrix);
        }

        @Override
        protected double[] getProjectedImage(int[][] imageGrayscale, int counted) {
            return super.getProjectedImage(imageGrayscale, counted);
        }
    }

    @Test
    /** Tests the method that calculates the eigen faces of set of face images. */
    public void testGetEigenfaces() {
        PcaTransformerMock pcaTransformer = new PcaTransformerMock();
        List<EigenFace> eigenFacesGrayscale = pcaTransformer.getEigenFacesGrayscale();
        double[][] covarianceMatrix = getEigenFacesCovarianceMatrix(pcaTransformer);
        double prevEigenValue = Double.POSITIVE_INFINITY;
        for (EigenFace eigenFace : eigenFacesGrayscale) {
            assertEigenFace(eigenFace, covarianceMatrix);
            assertTrue("The eigen values should be sorted in decreasing order",
                    eigenFace.eigenValue < prevEigenValue);
            assertTrue("The eigen values should all be positive", eigenFace.eigenValue > -EPSILON);
            prevEigenValue = eigenFace.eigenValue;
        }
    }

    @Test
    /** Tests the get image projection method. */
    public void testGetProjection() {
        PcaTransformerMock pcaTransformer = new PcaTransformerMock();
        File imageDirectory = new File(FIXTURE_PICTURE_DIRECTORY);
        List<File> imageFiles = new ArrayList<File>();

        for (String imageFileName : imageDirectory.list()) {
            String imageFilePath = imageDirectory.getPath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (imageFile.isDirectory()) {
                continue;
            }
            imageFiles.add(imageFile);
        }

        int h = FIXTURE_PICTURE_HEIGHT;
        int w = FIXTURE_PICTURE_WIDTH;
        for (File imageFile : imageFiles) {
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
            int [][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            double[] projectedImage =
                    pcaTransformer.getProjectedImage(grayscale, imageFiles.size());
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    assertEquals("The projected image is not equal to the original one",
                            grayscale[i][j], projectedImage[i * w + j], EPSILON);
                }
            }
        }
    }

    @Test
    /** Tests the methdo that gets the grayscale values of the input images and normalizes them. */
    public void testGetGrayscales() {
        PcaTransformerMock pcaTransformer =
                new PcaTransformerMock("fixtures" + File.separator + "get_grayscale");
        double[][] imageGrayscales = pcaTransformer.getImageGrayscales();
        assertEquals("The grayscales number is not as expected", 2, imageGrayscales.length);
        int [][] expectedColors = {{-38, 12}, {38, -12}};
        for (int i = 0; i < 2; i++) {
            assertEquals("The number of elements in the grayscale is not as expected",
                    FIXTURE_PICTURE_HEIGHT * FIXTURE_PICTURE_WIDTH, imageGrayscales[i].length);
            for (int j = 0; j < FIXTURE_PICTURE_HEIGHT; j++) {
                for (int k = 0; k < FIXTURE_PICTURE_WIDTH; k++) {
                    assertEquals("The grayscale pixel is not as expected",
                            expectedColors[i][(j + k) % 2],
                            imageGrayscales[i][j * FIXTURE_PICTURE_WIDTH + k],
                            EPSILON);
                }
            }
        }
    }

    @Test
    /** Tests the method that constructs the covariance matrix. */
    public void testGetCovarianceMatrix() {
        double [][] input = {{90, 60, 90}, {90, 90, 30}, {60, 60, 60}, {60, 60, 90}, {30, 30, 30}};
        double [][] expected = {{2520, 1800, 900}, {1800, 1800, 0}, {900, 0, 3600}};
        normalizeVectors(input);
        double[][] actualCovariance = getCovarianceMatrix(input);
        assertEquals("Actual covariance matrix has unexpected row count", expected.length,
                actualCovariance.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Actual covariance matrix has unexpected column count on row " + i,
                    expected[i].length, actualCovariance[i].length);
        }
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                assertEquals("The two covariance matrices have unequal element", expected[i][j],
                        actualCovariance[i][j], EPSILON);
            }
        }
    }

    @Test
    /** Tests the check for eigen vector. */
    public void testEigenVectorCheck() {
        double [][] matrix = {{1, 2, 1}, {6, -1, 0}, {-1, -2, -1}};
        double [][] vectors = {{-1, 2, 1}, {2, 3, -2}, {1, 6, -13} };
        double [] eigenValues = {-4, 3, 0};
        for (int i = 0; i < 3; i++) {
            assertEigenVector(vectors[i], eigenValues[i], matrix);
        }
    }

    @Test
    /** Tests that the usage of JAMA library is correct. */
    public void testJamaEigenVectors() {
        double [][] matrix = {{1, 2, 1}, {6, -1, 0}, {-1, -2, -1}};
        double [][] vectors = {{-1, 2, 1}, {2, 3, -2}, {1, 6, -13} };
        double [] eigenValues = {-4, 3, 0};
        PcaTransformerMock pcaTransformer = new PcaTransformerMock();
        EigenBean[] eigens = pcaTransformer.calculateEigens(matrix);

        for (int i = 0; i < 3; i++) {
            assertEquals("The length of the eigen vector is not as expected", vectors[i].length,
                    eigens[i].eigenVector.length);
            double closest = Double.POSITIVE_INFINITY;
            int index = 0;
            for (int k = 0; k < vectors.length; k++) {
                double minm = Double.POSITIVE_INFINITY;
                double maxm = Double.NEGATIVE_INFINITY;
                for (int j = 0; j < vectors[i].length; j++) {
                    if (Math.abs(vectors[i][j]) < EPSILON) {
                        assertEquals("The eigen vectors are not as expected", 0.0,
                                eigens[k].eigenVector[j], EPSILON);
                    } else {
                        minm = Math.min(minm, eigens[k].eigenVector[j] / vectors[i][j]);
                        maxm = Math.max(maxm, eigens[k].eigenVector[j] / vectors[i][j]);
                    }
                }
                if (maxm - minm < closest) {
                    closest = maxm - minm;
                    index = k;
                }
            }
            assertEquals("The eigen vectors are not as expected", 0.0, closest, EPSILON);
            assertEquals("The eigen values are not as expected", eigenValues[i],
                    eigens[index].eigenValue, EPSILON);
        }
    }

    private void assertEigenFace(EigenFace eigenFace, double[][] covarianceMatrix) {
        double eigenValue = eigenFace.eigenValue;
        double [] eigenVector = eigenFace.eigenFacePixels.clone();
        assertEigenVector(eigenVector, eigenValue, covarianceMatrix);
    }

    private void assertEigenVector(double [] eigenVector, double eigenValue, double [][] matrix) {
        double[] resultingVector = multVectorMatrix(eigenVector, matrix);
        assertEquals("The two vectors' dimensions do not match",
                resultingVector.length, eigenVector.length);
        double minm = Double.POSITIVE_INFINITY;
        double maxm = Double.NEGATIVE_INFINITY;

        int cnt = 0;
        for (int i = 0; i < eigenVector.length; i++) {
            if (Math.abs(eigenVector[i]) < EPSILON) {
                cnt++;
                continue;
            }
            minm = Math.min(minm, resultingVector[i] / eigenVector[i]);
            maxm = Math.max(maxm, resultingVector[i] / eigenVector[i]);
        }
        if (cnt < eigenVector.length) {
            if (Math.abs(maxm) > EPSILON) {
                assertEquals("The minimum and maximum of the ratio are too distant", 1.0, maxm
                        / minm, EPSILON);
                assertEquals("The eigenvalue is not calculated properly", 1.0, eigenValue / minm,
                        EPSILON);
            } else {
                assertEquals("The minimum and maximum of the ratio are too distant", 0.0, maxm,
                        EPSILON);
                assertEquals("The eigenvalue is not calculated properly", 0.0, eigenValue, EPSILON);
            }
        } else {
            for (int i = 0; i < resultingVector.length; i++) {
                assertEquals("The eigen vector should comprise only of zeroes", 0.0,
                        resultingVector[i], EPSILON);
            }
        }
    }

    private double [] multVectorMatrix(double[] vector, double[][] matrix) {
        int h = matrix.length;
        int w = matrix[0].length;
        assertEquals("The dimensions of matrix&vector to multiply do not match", vector.length, w);
        double [] res = new double [h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                res[i] += matrix[i][j] * vector[j];
            }
        }
        return res;
    }

    private double [][] getEigenFacesCovarianceMatrix(PcaTransformerMock pcaTransformer) {
        double[][] imageGrayscales = pcaTransformer.getImageGrayscales();
        return getCovarianceMatrix(imageGrayscales);
    }

    private void normalizeVectors(double [][] vectors) {
        for (int i = 0; i < vectors[0].length; i++) {
            double sum = 0;
            for (int j = 0; j < vectors.length; j++) {
                sum += vectors[j][i];
            }
            sum /= vectors.length;
            for (int j = 0; j < vectors.length; j++) {
                vectors[j][i] -= sum;
            }
        }
    }

    private double [][] getCovarianceMatrix(double [][] vectors) {
        int covarianceMatrixSide = vectors[0].length;
        double [][] covarianceMatrix = new double [covarianceMatrixSide][covarianceMatrixSide];
        int m = vectors.length;

        for (int i = 0; i < covarianceMatrixSide; i++) {
            for (int j = 0; j < covarianceMatrixSide; j++) {
                covarianceMatrix[i][j] = 0;
                for (int k = 0; k < m; k++) {
                    covarianceMatrix[i][j] += vectors[k][i] * vectors[k][j];
                }
            }
        }

        for (int i = 0; i < covarianceMatrixSide; i++) {
            for (int j = 0; j < covarianceMatrixSide; j++) {
                assertEquals("The covariance matrix is not symmetric", covarianceMatrix[i][j],
                        covarianceMatrix[j][i], EPSILON);
            }
        }
        return covarianceMatrix;
    }
}
