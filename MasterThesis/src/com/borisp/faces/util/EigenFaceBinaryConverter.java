package com.borisp.faces.util;

import java.nio.ByteBuffer;


/**
 * Class defining methods to convert images to / from binary array.
 *
 * @author Boris
 */
public class EigenFaceBinaryConverter {

    public static byte [] getFaceBytes(double [] facePixels) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(facePixels.length * 8);
        for (int i = 0; i < facePixels.length; i++) {
            byteBuffer.putDouble(facePixels[i]);
        }
        return byteBuffer.array();
    }

    public static double[] constructFaceFromBytes(byte [] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        double [] toReturn = new double[bytes.length / 8];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = byteBuffer.getDouble();
        }
        return toReturn;
    }
}
