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
    private Integer classificationValueId;

    @Column(name = "classification_value_value")
    private String value;

    // bi-directional many-to-one association to Classification bean
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_fk")
    private Classification classification;

    public Integer getClassificationValueId() {
        return classificationValueId;
    }

    public void setClassificationValueId(Integer classificationValueId) {
        this.classificationValueId = classificationValueId;
    }

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
}
