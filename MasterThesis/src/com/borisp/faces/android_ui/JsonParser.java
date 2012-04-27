package com.borisp.faces.android_ui;

import java.util.List;

import com.google.gson.Gson;

/**
 * A class that will be used to serialize / deserialize the jsons that serve as input / output.
 *
 * @author Boris
 */
public class JsonParser {
    private Gson gson;

    public JsonParser() {
        this.gson = new Gson();
    }

    /**
     * Serializes the given list of faces in a string.
     *
     * @param faces The array which to serialize.
     * @return The json serialized version of the list.
     */
    public String serializeFaces(List<Face> faces) {
        return gson.toJson(faces);
    }

    /**
     * Deserializes the given json string to its corresponding array of {@link Face}s.
     *
     * @param facesJson The serialized version of the faces array.
     * @return The deserialized array.
     */
    public Face[] deserializeFaces(String facesJson) {
        return gson.fromJson(facesJson, Face[].class);
    }
}
