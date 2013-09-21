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
@Table(name = "image_groups")
/** A bean that will allow to group the raw images. */
public class ImageGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_group_pk")
    private Integer imageGroupId;

    // bi-directional many-to-one association to image bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "imageGroup", cascade = { CascadeType.DETACH,
            CascadeType.REMOVE }, orphanRemoval = true)
    private List<Image> images;

    @Column(name = "image_group_key")
    private String key;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getImageGroupId() {
        return imageGroupId;
    }

    public void setImageGroupId(Integer imageGroupId) {
        this.imageGroupId = imageGroupId;
    }
}
