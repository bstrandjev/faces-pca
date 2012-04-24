package com.borisp.faces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;

import com.borisp.faces.pca.PcaTransformer;
import com.borisp.faces.util.ColorPixel;
import com.borisp.faces.util.GrayscaleConverter;
import com.borisp.faces.util.ImageReader;
import com.borisp.faces.util.ImageWriter;
import com.google.gson.Gson;

public class Main {
    private static final String MANIPULATED_IMAGES_DIRECTORY_DEFAULT = "images" + File.separator
            + "manipulated";
    private static final String PCA_CEFICIENTS_FILE = "coeficients/coef_all.txt";

    @SuppressWarnings("unused")
    private static void createTestImages() {
        int h = 30;
        int w = 22;
        Integer [][] grayScales = new Integer [h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                grayScales[i][j] = (i + j) % 2 == 0 ? 25 : 225;
            }
        }
        ImageWriter.createImage("sample1.bmp", grayScales, true);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                grayScales[i][j] = (i + j) % 2 == 0 ? 101 : 201;
            }
        }
        ImageWriter.createImage("sample2.bmp", grayScales, true);
    }

    public static void main(String[] args) throws ParseException, IOException {
        String json = "[[123.3, 234.5], [234.1, 456.5]]";
        Gson gson = new Gson();
        double [][] points = gson.fromJson(json, double[][].class);
        System.out.println(points[1][1]);
        //ClassificationApplicationPreparator.prepareClassificationApplication();
//        createTestImages();
//        PcaTransformer pcaTransformer =
//                new PcaTransformer(ImageScaler.TARGET_HEIGHT, ImageScaler.TARGET_WIDTH);
//        findProjectImages(pcaTransformer);
//        findImageCoeficients(pcaTransformer);
//        pcaTransformer.printEigenFaces();
//        ImageManipulator imageManipulator = new ImageManipulator();
//        imageManipulator.manipulateImages();
    }

    private static void findImageCoeficients(PcaTransformer pcaTransformer) throws IOException {
        File imageDirectory = new File(MANIPULATED_IMAGES_DIRECTORY_DEFAULT);
        FileOutputStream output = new FileOutputStream(new File(PCA_CEFICIENTS_FILE));
        OutputStreamWriter writer = new OutputStreamWriter(output);
        for (String imageFileName : imageDirectory.list()) {
            String imageFilePath = imageDirectory.getPath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (imageFile.isDirectory()) {
                continue;
            }
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
            int [][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            double[] pcaCoeficients = pcaTransformer.getPcaCoeficients(grayscale);
            for (int i = 0; i < pcaCoeficients.length; i++) {
                writer.append(String.format("%.4f ", pcaCoeficients[i]));
            }
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }

    private static void findProjectImages(PcaTransformer pcaTransformer) throws IOException {
        File imageDirectory = new File(MANIPULATED_IMAGES_DIRECTORY_DEFAULT);
        for (String imageFileName : imageDirectory.list()) {
            String imageFilePath = imageDirectory.getPath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (imageFile.isDirectory()) {
                continue;
            }
            ColorPixel[][] imagePixels = ImageReader.getImagePixels(imageFile);
            int [][] grayscale = GrayscaleConverter.getImageGrayscale(imagePixels);
            pcaTransformer.printProjectedImage(grayscale, 15, imageFileName);
        }
    }
}
