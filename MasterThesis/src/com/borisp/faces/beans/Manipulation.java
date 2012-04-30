package com.borisp.faces.beans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Table(name = "manipulations")
/** A bean for representing manipulation: a operation that crops and resizes the images. */
public class Manipulation {
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
    private static final String MANIPULATION_LABEL_FORMAT = "%02d %s";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manipulations_pk")
    private Integer manipualtionId;

    @Column(name="created")
    private Timestamp created;

    @Column(name="manipulation_index")
    private Integer manipulationIndex;

    // bi-directional many-to-one association to ManipulatedImage bean
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manipulation",
               cascade = { CascadeType.DETACH, CascadeType.REMOVE }, orphanRemoval = true)
    private List<ManipulatedImage> manipulatedImages;

    @Override
    public String toString() {
        String dateCreated = DATE_FORMAT.format(new Date(getCreated().getTime()));
        return String.format(MANIPULATION_LABEL_FORMAT, getManipulationIndex(), dateCreated);
    }

    public Integer getManipualtionId() {
        return manipualtionId;
    }

    public void setManipualtionId(Integer manipualtionId) {
        this.manipualtionId = manipualtionId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public List<ManipulatedImage> getManipulatedImages() {
        return manipulatedImages;
    }

    public void setManipulatedImages(List<ManipulatedImage> manipulatedImages) {
        this.manipulatedImages = manipulatedImages;
    }

    public Integer getManipulationIndex() {
        return manipulationIndex;
    }

    public void setManipulationIndex(Integer manipulationIndex) {
        this.manipulationIndex = manipulationIndex;
    }
}
