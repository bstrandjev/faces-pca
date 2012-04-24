package com.borisp.faces.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "images")
/** A bean for the initial images. */
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "images_pk")
    private Integer imageId;

    @Column(name = "image_key")
    private String key;

    @Column(name = "real_image_path")
    private String imagePath;

    // bi-directional many-to-one association to ManipulatedImage bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalImage",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<ManipulatedImage> manipulatedImages;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ManipulatedImage> getManipulatedImages() {
        return manipulatedImages;
    }

    public void setManipulatedImages(List<ManipulatedImage> manipulatedImages) {
        this.manipulatedImages = manipulatedImages;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
