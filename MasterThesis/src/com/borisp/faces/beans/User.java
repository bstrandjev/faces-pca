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
@Table(name = "users")
/** A bean for representing a person how executed classification of the training set images */
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private Integer userId;

    @Column(name = "name")
    private String name;

    // bi-directional many-to-one association to Classification bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<ClassifiedImage> classifications;

    @Override
    public String toString() {
        return name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassifiedImage> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<ClassifiedImage> classifications) {
        this.classifications = classifications;
    }
}
