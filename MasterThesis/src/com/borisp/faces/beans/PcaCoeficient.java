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
@Table(name = "pca_coeficients")
/** A bean representing single coeficient from PCA transform for single manipulated image. */
public class PcaCoeficient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pca_coeficients_pk")
    private Integer pcaCoeficientId;

    @Column(name = "coeficient")
    private Double coeficient;

    //bi-directional many-to-one association to ManipulatedImage bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulated_image_fk")
    private ManipulatedImage manipulatedImage;

    //bi-directional many-to-one association to EigenFace bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="eigen_face_fk")
    private EigenFace eigenFace;

    public Integer getPcaCoeficientId() {
        return pcaCoeficientId;
    }

    public void setPcaCoeficientId(Integer pcaCoeficientId) {
        this.pcaCoeficientId = pcaCoeficientId;
    }

    public Double getCoeficient() {
        return coeficient;
    }

    public void setCoeficient(Double coeficient) {
        this.coeficient = coeficient;
    }

    public ManipulatedImage getManipulatedImage() {
        return manipulatedImage;
    }

    public void setManipulatedImage(ManipulatedImage manipulatedImage) {
        this.manipulatedImage = manipulatedImage;
    }

    public EigenFace getEigenFace() {
        return eigenFace;
    }

    public void setEigenFace(EigenFace eigenFace) {
        this.eigenFace = eigenFace;
    }
}
