package com.borisp.faces.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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

    // bi-directional many-to-one association to Classification bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transformation",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<EigenFaceEntity> eigenFaces;

    // bi-directional many-to-one association to TrasformedImage bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transformation",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<TransformedImage> transformedImages;

    @Override
    public String toString() {
        return getTransformationId().toString();
    }

    /** Returns all the manipulated images associated with this transformation. */
    public List<ManipulatedImage>  getAllManipulatedImages() {
        List<ManipulatedImage> manipulatedImages = new ArrayList<>();
        for (TransformedImage transformedImage : getTransformedImages()) {
            manipulatedImages.add(transformedImage.getManipulatedImage());
        }
        return manipulatedImages;
    }

    public Integer getTransformationId() {
        return transformationId;
    }

    public void setTransformationId(Integer transformationId) {
        this.transformationId = transformationId;
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

    public List<TransformedImage> getTransformedImages() {
        return transformedImages;
    }

    public void setTransformedImages(List<TransformedImage> transformedImages) {
        this.transformedImages = transformedImages;
    }

    /** Constructs list of all the ids of manipulated images that are used in the transformation. */
    public List<Integer> constructManipualtedImagesIndexList() {
        Set<Integer> indexSet = new TreeSet<Integer>();
        for (TransformedImage transformedImage : getTransformedImages()) {
            indexSet.add(transformedImage.getManipulatedImage().getManipulatedImageId());
        }
        return new ArrayList<Integer>(indexSet);
    }
}
