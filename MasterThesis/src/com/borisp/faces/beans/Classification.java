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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_classifications")
/** A bean for representing a classification in the database. */
public class Classification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_pk")
    private Integer classificationId;

    @Column(name = "classification_key")
    private String classificationKey;

    @Column(name = "classification_name")
    private String classificationName;

    // bi-directional many-to-one association to ClassificationValue bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classification",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<ClassificationValue> classificationValues;

    @Override
    public String toString() {
        return classificationKey;
    }

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public String getClassificationKey() {
        return classificationKey;
    }

    public void setClassificationKey(String classificationKey) {
        this.classificationKey = classificationKey;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public List<ClassificationValue> getClassificationValues() {
        return classificationValues;
    }

    public void setClassificationValues(List<ClassificationValue> classificationValues) {
        this.classificationValues = classificationValues;
    }

    /** Constructs list of all the ids of manipulated images that are mapped to this classification. */
    public List<Integer> constructManipualtedImagesIndexList() {
        Set<Integer> indexSet = new TreeSet<Integer>();
        for (ClassificationValue classificationValue : getClassificationValues()) {
            for (ClassifiedImage classifiedImage : classificationValue.getClassifiedImages()) {
                indexSet.add(classifiedImage.getManipulatedImage().getManipulatedImageId());
            }
        }
        return new ArrayList<Integer>(indexSet);
    }
}
