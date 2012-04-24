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
@Table(name = "classifications")
/** A bean for representing a classification of single manipulated image executed from a user */
public class Classification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_pk")
    private Integer classificationId;

    @Column(name="beautiful")
    private Byte isBeautiful;

    //bi-directional many-to-one association to ManipulatedImage bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulated_image_fk")
    private ManipulatedImage manipulatedImage;

    //bi-directional many-to-one association to User bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user")
    private User user;

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public Byte getIsBeautiful() {
        return isBeautiful;
    }

    public void setIsBeautiful(Byte isBeautiful) {
        this.isBeautiful = isBeautiful;
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
}
