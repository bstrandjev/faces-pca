package com.borisp.faces.classification;


/**
 * A class representing the face bean. Only basic information is stored in the bean.
 *
 * @author Boris
 */
public class Face {

    private String key;
    private int index;
    private Boolean beautiful;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Boolean getBeautiful() {
        return beautiful;
    }

    public void setBeautiful(Boolean beautiful) {
        this.beautiful = beautiful;
    }
}
