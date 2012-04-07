package com.borisp.faces.beans;

import android.app.Activity;
import android.widget.ImageView;

/**
 * A class representing the face bean. Only basic information is stored in the bean.
 *
 * @author Boris
 */
public class Face {
    private static final String RES_IMAGE_FORMAT = "img%d";

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

    public void loadFaceImageView(Activity activity, int imageViewId) {
        String currentPictureName = String.format(RES_IMAGE_FORMAT, index);

        int id = activity.getResources()
                .getIdentifier(currentPictureName, "drawable", activity.getPackageName());
        ImageView image = (ImageView)activity.findViewById(imageViewId);
        image.setImageResource(id);
    }

    public Boolean getBeautiful() {
        return beautiful;
    }

    public void setBeautiful(Boolean beautiful) {
        this.beautiful = beautiful;
    }
}
