package com.borisp.faces.util;


/**
 * Class defining methods to convert images to / from binary array.
 *
 * @author Boris
 */
public class EigenFaceBinaryConverter {

    public static byte [] getEigenFaceBytes(double [] facePixels) {
        int n = facePixels.length;
        byte [] bytes = new byte[n * 8];
        for (int i = 0; i < n; i++) {
            long l = Double.doubleToLongBits(facePixels[i]);
            for (int j = 0; j < 8; j++) {
                bytes[i * 8 + j] = (byte)((l >> j) & 0xFF);
            }
        }
        return bytes;
    }

    public static double[] constructEigenface(byte [] bytes){
        double [] toReturn = new double[bytes.length / 8];
        int n = toReturn.length;
        for (int i = 0; i < n; i++) {
            long l = 0;
            for (int j = 0; j < 8; j++) {
                l |= (long)bytes[i * 8 + j] << j;
            }
            toReturn[i] = Double.longBitsToDouble(l);
        }
        return toReturn;
    }
}
