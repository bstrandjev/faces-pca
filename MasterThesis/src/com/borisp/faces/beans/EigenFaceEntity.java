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
@Table(name = "eigen_faces")
/** A bean representing a single eigen face. */
public class EigenFaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eigen_faces_pk")
    private Integer eigenFaceId;

    @Lob
    @Column(name = "eigen_face")
    private byte[] eigenFace;

    @Column(name = "eigen_value")
    private Double eigenValue;

    //bi-directional many-to-one association to Transformation bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="transformation_fk")
    private Transformation transformation;

    // bi-directional many-to-one association to PcaCoeficient bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "eigenFace",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<PcaCoeficient> pcaCoeficients;

    public Integer getEigenFaceId() {
        return eigenFaceId;
    }

    public void setEigenFaceId(Integer eigenFaceId) {
        this.eigenFaceId = eigenFaceId;
    }

    public Double getEigenValue() {
        return eigenValue;
    }

    public void setEigenValue(Double eigenValue) {
        this.eigenValue = eigenValue;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public List<PcaCoeficient> getPcaCoeficients() {
        return pcaCoeficients;
    }

    public void setPcaCoeficients(List<PcaCoeficient> pcaCoeficients) {
        this.pcaCoeficients = pcaCoeficients;
    }

    protected String getEigenFace() {
        return PersistenceHelper.getContentFromByte(eigenFace);
    }

    protected void setEigenFace(String binaryValue) {
        this.eigenFace = PersistenceHelper.getContentfromString(binaryValue);
    }

    public double[] getFacePixels() {
        return EigenFaceBinaryConverter.constructEigenface(this.eigenFace);
    }

    public void setFacePixels(double [] facePixels) {
        this.eigenFace = EigenFaceBinaryConverter.getEigenFaceBytes(facePixels);
    }
}
