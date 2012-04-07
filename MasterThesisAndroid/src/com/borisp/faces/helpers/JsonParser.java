package com.borisp.faces.helpers;

import android.content.Context;

import com.borisp.faces.beans.Face;
import com.google.gson.Gson;

/**
 * A class that will be used to serialize / deserialize the jsons that serve as input / output.
 *
 * @author Boris
 */
public class JsonParser {
    private static final String FACE_JSON_PATH = "faces.json";
    private Gson gson;
    private Context context;

    public JsonParser(Context context) {
        this.gson = new Gson();
        this.context = context;
    }

    /**
     * @return The array of faces input to the application.
     */
    public Face[] getInputFaceData() {
        String facesJson = IOHelper.getFileContent(context.getAssets(), FACE_JSON_PATH);
        return gson.fromJson(facesJson, Face[].class);
    }

    /**
     * Serializes the given array of faces in a string.
     *
     * @param faces The array which to serialize.
     * @return The json serialized version of the array.
     */
    public String serializeFaces(Face[] faces) {
        return gson.toJson(faces);
    }

    /**
     * Serializes the given face to a string.
     *
     * @param faces The face which to serialize.
     * @return The json serialized version of the face.
     */
    public String serializeFace(Face face) {
        return gson.toJson(face);
    }
}
