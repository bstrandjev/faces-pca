package com.borisp.faces.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "transformed_images")
/** Represents the mapping between a transformation and all images included in it. */
public class TransformedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transformed_images_pk")
    private Integer transformedImageId;

    //bi-directional many-to-one association to ManipulatedImage bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulated_image_fk")
    private ManipulatedImage manipulatedImage;

    //bi-directional many-to-one association to Transformation bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="transformation_fk")
    private Transformation transformation;

    public Integer getTransformedImageId() {
        return transformedImageId;
    }

    public void setTransformedImageId(Integer transformedImageId) {
        this.transformedImageId = transformedImageId;
    }

    public ManipulatedImage getManipulatedImage() {
        return manipulatedImage;
    }

    public void setManipulatedImage(ManipulatedImage manipulatedImage) {
        this.manipulatedImage = manipulatedImage;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }
}
