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
}
