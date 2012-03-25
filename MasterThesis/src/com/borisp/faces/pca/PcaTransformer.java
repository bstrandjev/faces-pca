package com.borisp.faces.pca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;

public class PcaTransformer {
    /** The directory which stores the manipulated sample pictures. */
    private static final String IMAGES_DIRECTORY_DEFAULT = "images" + File.separator + "manipulated";

    // Constant
    private final int IMAGE_HEIGHT;
    private final int IMAGE_WIDTH;
    private final String IMAGES_DIRECTORY;

    private double [] average;
    private List<EigenFace> eigenFaces;

    /** A class representing a single eigen face. */
    public class EigenFace implements Comparable<EigenFace> {
        public double[] eigenFacePixels;
        public Double eigenValue;

        public EigenFace(EigenBean eigenBean, double[][] imageGrayscales) {
            this.eigenValue = eigenBean.eigenValue;
            int w = IMAGE_WIDTH;
            int h = IMAGE_HEIGHT;
            int m = eigenBean.eigenVector.length;
            this.eigenFacePixels = new double[h * w];
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < w * h; k++) {
                    eigenFacePixels[k] += eigenBean.eigenVector[j] * imageGrayscales[j][k];
                }
            }
            //normalize the vector
            double length = Math.sqrt(multiplyVectors(eigenFacePixels, eigenFacePixels));
            if (Math.abs(length) > 1e-7) {
                for (int i = 0; i < w * h; i++) {
                    eigenFacePixels[i] /= length;
                }
            }
        }

        @Override
        public int compareTo(EigenFace other) {
            return -eigenValue.compareTo(other.eigenValue);
        }
    }

    public class EigenBean {
        public double[] eigenVector;
        public double eigenValue;
    }

    public PcaTransformer(int imageHeight, int imageWidth) {
        this(imageHeight, imageWidth, IMAGES_DIRECTORY_DEFAULT);
    }

    public PcaTransformer(int imageHeight, int imageWidth, String imageDirectory) {
        this.IMAGE_HEIGHT = imageHeight;
        this.IMAGE_WIDTH = imageWidth;
        this.IMAGES_DIRECTORY = imageDirectory;
        this.eigenFaces = getEigenFacesGrayscale();
    }

    public void printProjectedImage(int [][] imageGrayscale, int counted, String fileName) {
        double [] projection = getProjectedImage(imageGrayscale, counted);
        ImageWriter.createImage("projected" + File.separator + fileName,
                normalizeForPrinting(projection), IMAGE_HEIGHT, IMAGE_WIDTH,
                false);
    }

    /** Calculates the projection of the given image in restricted PCA space. */
    protected double[] getProjectedImage(int [][] imageGrayscale, int counted) {
        double [] projection = new double[eigenFaces.get(0).eigenFacePixels.length];
        double [] coeficients = getPcaCoeficients(imageGrayscale);
        for (int i = 0; i < counted; i++) {
            for (int j = 0; j < projection.length; j++) {
                projection[j] += coeficients[i] * eigenFaces.get(i).eigenFacePixels[j];
            }
        }
        for (int i = 0; i < projection.length; i++) {
            projection[i] += average[i];
        }
        return projection;
    }

    /** Does an actual transformation from image to coeficients. */
    public double [] getPcaCoeficients(int [][] imageGrayscale) {
        int h = IMAGE_HEIGHT;
        int w = IMAGE_WIDTH;
        double [] normalizedImage = new double [h * w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                normalizedImage[i * w + j] = imageGrayscale[i][j] - average[i * w + j];
            }
        }
        double [] res = new double[eigenFaces.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = multiplyVectors(eigenFaces.get(i).eigenFacePixels, normalizedImage);
        }
        return res;
    }

    /** A method that can be called to display the eigen faces calculated from the training set. */
    public void printEigenFaces() {
        int idx = 1;
        for (EigenFace eigenFace : eigenFaces) {
            ImageWriter.createImage(String.format("eigen_face%03d.jpg", idx),
                    normalizeForPrinting(eigenFace.eigenFacePixels), IMAGE_HEIGHT, IMAGE_WIDTH,
                    false);
            idx++;
        }
    }

    /** This method constructs the list of eigen faces out of the training set. */
    protected List<EigenFace> getEigenFacesGrayscale() {
        double[][] imageGrayscales = getImageGrayscales();
        double[][] sampleMatrixArray = sampleMatrix(imageGrayscales);
        EigenBean[] eigens = calculateEigens(sampleMatrixArray);
        List<EigenFace> eigenFaces = new ArrayList<EigenFace>();
        for (EigenBean eigenBean : eigens) {
            eigenFaces.add(new EigenFace(eigenBean, imageGrayscales));
        }
        Collections.sort(eigenFaces);
        return eigenFaces;
    }

    /** Finds the eigen values and eigen vectors of the given matrix. */
    protected EigenBean[] calculateEigens(double [][] inputMatrix) {
        Matrix sampleMatrix = new Matrix(inputMatrix);
        EigenvalueDecomposition eigenMatrix = new EigenvalueDecomposition(sampleMatrix);
        Matrix eigenVectorMatrix = eigenMatrix.getV();
        double[] realEigenvalues = eigenMatrix.getRealEigenvalues();
        int m = eigenVectorMatrix.getRowDimension();

        // Calculating the eigen vectors and values
        EigenBean[] eigens = new EigenBean[m];
        for (int i = 0; i < m; i++) {
            eigens[i] = new EigenBean();
            eigens[i].eigenVector = new double[m];
            for (int j = 0; j < m; j++) {
                eigens[i].eigenVector[j] = eigenVectorMatrix.get(j, i);
            }
            eigens[i].eigenValue = realEigenvalues[i];
        }
        return eigens;
    }

    /**
     * A method that changes the eigen face pixels so that they fit in the range [0, 255].
     * It does some tweaking of pixel values, so it should be used only for preparing for printing.
     */
    private double[] normalizeForPrinting(double[] eigenFace) {
        double[] toRet = eigenFace.clone();
        double maxm = 0.0;
        double minm = Double.MIN_VALUE;
        for (int i = 0; i < toRet.length; i++) {
            maxm = Math.max(maxm, toRet[i]);
            minm = Math.min(minm, toRet[i]);
        }
        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = ((toRet[i] - minm)* 255.0) / (maxm - minm);
        }
        return toRet;
    }

    /** Calculate the auxiliary matrix L as per the article. */
    private double[][] sampleMatrix(double [][] imageGrayScales) {
        int sampleCount = imageGrayScales.length;
        double [][] res = new double[sampleCount][sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            for (int j = 0; j < sampleCount; j++) {
                res[i][j] = multiplyVectors(imageGrayScales[i], imageGrayScales[j]);
            }
        }
        return res;
    }

    /** Calculates a cell in the auxiliary matrix L. */
    private double multiplyVectors(double [] imageArray1, double [] imageArray2) {
        double res = 0;
        for (int i = 0; i < imageArray1.length; i++) {
            res += imageArray1[i] * imageArray2[i];
        }
        return res;
    }

    /**
     * Fetches all sample images after the initial manipulation.
     * The average face is calculated and subtracted from every image.
     *
     * @return An array containing an element for each image. Each image is represented by a one
     *      dimensional array of its pixels.
     */
    protected double [][] getImageGrayscales() {
        File imageDirectory = new File(IMAGES_DIRECTORY);
        List<double []> imageGrayscales = new ArrayList<>();
        int w = IMAGE_WIDTH;
        int h = IMAGE_HEIGHT;
        this.average = new double [h * w];
        for (String imageFileName : imageDirectory.list()) {
            String imageFilePath = imageDirectory.getPath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (imageFile.isDirectory()) {
                continue;
            }
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
            int [][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            double [] dGrayscale = new double [h * w];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    average[i * w + j] += grayscale[i][j];
                    dGrayscale[i * w + j] = grayscale[i][j];
                }
            }
            imageGrayscales.add(dGrayscale);
        }

        int imageCount = imageGrayscales.size();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                average[i * w + j] /= imageCount;
            }
        }
        // debug output of the image
        ImageWriter.createImage("average.jpg", average, h, w, false);
        for (double [] dGrayscale : imageGrayscales) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    dGrayscale[i * w + j] -= average[i * w + j];
                }
            }
        }
        double [][] toRet = new double[imageGrayscales.size()][];
        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = imageGrayscales.get(i);
        }
        return toRet;
    }
}
