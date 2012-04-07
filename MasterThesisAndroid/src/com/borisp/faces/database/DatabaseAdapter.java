package com.borisp.faces.database;

import com.borisp.faces.beans.Face;

/**
 * Defines the methods with which the database can be accessed.
 *
 * @author Boris
 */
public interface DatabaseAdapter {

    /**
     * Stores the given face in the database.
     *
     * If its key already exists in the database an update is performed.
     *
     * @param face The face which to store in the database.
     */
    void storeFace(Face face);

    /**
     * Reads the face with the given index from the database.
     *
     * @param index The index of the face which to fetch
     * @return The sought face, or null if no such exist.
     */
    Face readFace(int index);

    /**
     * @return An array loaded with all the faces from the database.
     */
    Face [] loadAllFaces();
}
