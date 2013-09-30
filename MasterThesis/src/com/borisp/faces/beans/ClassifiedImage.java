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
@Table(name = "classified_images")
/** A bean for representing a classification of single manipulated image executed from a user */
public class ClassifiedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classified_image_pk")
    private Integer classifiedImageId;

    //bi-directional many-to-one association to ManipulatedImage bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulated_image_fk")
    private ManipulatedImage manipulatedImage;

    //bi-directional many-to-one association to User bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_fk")
    private User user;

    //bi-directional many-to-one association to ClassificationValue bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="classification_value_fk")
    private ClassificationValue classificationValue;

    public Integer getClassifiedImageId() {
        return classifiedImageId;
    }

    public void setClassifiedImageId(Integer classifiedImageId) {
        this.classifiedImageId = classifiedImageId;
    }

    public ManipulatedImage getManipulatedImage() {
        return manipulatedImage;
    }

    public void setManipulatedImage(ManipulatedImage manipulatedImage) {
        this.manipulatedImage = manipulatedImage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClassificationValue getClassificationValue() {
        return classificationValue;
    }

    public void setClassificationValue(ClassificationValue classificationValue) {
        this.classificationValue = classificationValue;
    }
}
