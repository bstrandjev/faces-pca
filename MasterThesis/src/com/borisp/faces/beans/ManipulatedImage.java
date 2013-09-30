package com.borisp.faces.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "manipulated_images")
/** A bean for the cropped and resized images. */
public class ManipulatedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manipulated_images_pk")
    private Integer manipulatedImageId;

    @Column(name = "manipulated_img_path")
    private String manipulatedImagePath;

    @Column(name="is_good")
    private Byte isGood;

    @Column(name = "begX")
    private Integer begX;

    @Column(name = "begY")
    private Integer begY;

    @Column(name = "endX")
    private Integer endX;

    @Column(name = "endY")
    private Integer endY;

    //bi-directional many-to-one association to Image bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="image_fk")
    private Image originalImage;

    //bi-directional many-to-one association to Manipulation bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulation_fk")
    private Manipulation manipulation;

    // bi-directional many-to-one association to Classification bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manipulatedImage",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<ClassifiedImage> classifications;

    // bi-directional many-to-one association to PcaCoeficient bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manipulatedImage",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<PcaCoeficient> pcaCoeficients;

    public Integer getManipulatedImageId() {
        return manipulatedImageId;
    }

    public void setManipulatedImageId(Integer manipulatedImageId) {
        this.manipulatedImageId = manipulatedImageId;
    }

    public Byte getIsGood() {
        return isGood;
    }

    public void setIsGood(Byte isGood) {
        this.isGood = isGood;
    }

    public Integer getBegX() {
        return begX;
    }

    public void setBegX(Integer begX) {
        this.begX = begX;
    }

    public Integer getBegY() {
        return begY;
    }

    public void setBegY(Integer begY) {
        this.begY = begY;
    }

    public Integer getEndX() {
        return endX;
    }

    public void setEndX(Integer endX) {
        this.endX = endX;
    }

    public Integer getEndY() {
        return endY;
    }

    public void setEndY(Integer endY) {
        this.endY = endY;
    }

    public Image getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

    public Manipulation getManipulation() {
        return manipulation;
    }

    public void setManipulation(Manipulation manipulation) {
        this.manipulation = manipulation;
    }

    public List<ClassifiedImage> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<ClassifiedImage> classifications) {
        this.classifications = classifications;
    }

    public List<PcaCoeficient> getPcaCoeficients() {
        return pcaCoeficients;
    }

    public void setPcaCoeficients(List<PcaCoeficient> pcaCoeficients) {
        this.pcaCoeficients = pcaCoeficients;
    }

    public String getManipulatedImagePath() {
        return manipulatedImagePath;
    }

    public void setManipulatedImagePath(String manipulatedImagePath) {
        this.manipulatedImagePath = manipulatedImagePath;
    }
}
