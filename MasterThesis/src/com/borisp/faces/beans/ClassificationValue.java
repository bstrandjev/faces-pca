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
@Table(name = "classification_values")
/** A bean for representing one of the enumerated values of a classification in the database. */
public class ClassificationValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_value_pk")
    private Integer classificationValuePk;

    @Column(name = "classification_value_value")
    private String value;

    @Column(name = "classification_value_id")
    private int classificationValueId;

    // bi-directional many-to-one association to Classification bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_fk")
    private Classification classification;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public int getClassificationValueId() {
        return classificationValueId;
    }

    public void setClassificationValueId(int classificationValueId) {
        this.classificationValueId = classificationValueId;
    }

    public Integer getClassificationValuePk() {
        return classificationValuePk;
    }

    public void setClassificationValuePk(Integer classificationValuePk) {
        this.classificationValuePk = classificationValuePk;
    }
}
