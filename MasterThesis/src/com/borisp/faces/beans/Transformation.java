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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.borisp.faces.util.EigenFaceBinaryConverter;
import com.borisp.faces.util.PersistenceHelper;

@Entity
@Table(name = "transformations")
/** A bean for representing the PCA transformation of a manipulation */
public class Transformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transformations_pk")
    private Integer transformationId;

    @Lob
    @Column(name = "average_face")
    private byte[] averageFace;

    //bi-directional many-to-one association to Manipulation bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manipulation_fk")
    private Manipulation manipulation;

    // bi-directional many-to-one association to Classification bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transformation",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<EigenFaceEntity> eigenFaces;

    @Override
    public String toString() {
        return getManipulation() + " " + getTransformationId();
    }

    public Integer getTransformationId() {
        return transformationId;
    }

    public void setTransformationId(Integer transformationId) {
        this.transformationId = transformationId;
    }

    public Manipulation getManipulation() {
        return manipulation;
    }

    public void setManipulation(Manipulation manipulation) {
        this.manipulation = manipulation;
    }

    public List<EigenFaceEntity> getEigenFaces() {
        return eigenFaces;
    }

    public void setEigenFaces(List<EigenFaceEntity> eigenFaces) {
        this.eigenFaces = eigenFaces;
    }

    protected String getAverageFace() {
        return PersistenceHelper.getContentFromByte(averageFace);
    }

    protected void setAverageFace(String binaryValue) {
        this.averageFace = PersistenceHelper.getContentfromString(binaryValue);
    }

    public double[] getAverageFacePixels() {
        return EigenFaceBinaryConverter.constructFaceFromBytes(this.averageFace);
    }

    public void setAverageFacePixels(double [] facePixels) {
        this.averageFace = EigenFaceBinaryConverter.getFaceBytes(facePixels);
    }
}
