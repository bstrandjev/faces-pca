package com.borisp.faces.util;


public class PersistenceHelper {
    public static byte[] getContentfromString(String string) {
        byte[] data = javax.xml.bind.DatatypeConverter.parseBase64Binary(string);
        return data;
    }

    public static String getContentFromByte(byte[] data) {
        String media = javax.xml.bind.DatatypeConverter.printBase64Binary(data);
        return media;
    }

}
