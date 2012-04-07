package com.borisp.faces.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.borisp.faces.beans.Face;
import com.borisp.faces.helpers.JsonParser;

/**
 * Defines the methods with which the database can be accessed.
 *
 * @author Boris
 */
public class DatabaseAdapterImpl implements DatabaseAdapter {
    // Where clauses constants
    private static final String FACES_WHERE = DatabaseConstants.FACES_INDEX + " = ?";

    private Context context;

    public DatabaseAdapterImpl(Context context) {
        this.context = context;
    }

    public void storeFace(Face face) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        storeFace(database, face);
        database.close();
    }

    /**
     * Stores the given face in the database.
     *
     * If its key already exists in the database an update is performed.
     *
     * This method is intentionally left package protected so the {@link DatabaseHelper} can access
     * it.
     *
     * @param database The database in which to store the given face
     * @param face The face which to store in the database.
     */
    void storeFace(SQLiteDatabase database, Face face) {
        // Low performant logging. And so what?
        Log.v(DatabaseConstants.LOG_TAG, "Storing face in the database: "
                + new JsonParser(context).serializeFace(face));
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.FACES_KEY, face.getKey());
        values.put(DatabaseConstants.FACES_INDEX, face.getIndex());
        if (face.getBeautiful() != null) {
            values.put(DatabaseConstants.FACES_BEAUTIFUL, face.getBeautiful() ? 1 : 0);
        } else {
            values.putNull(DatabaseConstants.FACES_BEAUTIFUL);
        }

        if (readFace(database, face.getIndex()) != null) {
            String [] whereArgs = { String.valueOf(face.getIndex()) };
            database.update(DatabaseConstants.FACES_TABLE, values, FACES_WHERE, whereArgs);
        } else {
            database.insert(DatabaseConstants.FACES_TABLE, null, values);
        }
    }

    public Face readFace(int index) {
        return readFace(null, index);
    }

    private Face readFace(SQLiteDatabase database, int index) {
        Log.v(DatabaseConstants.LOG_TAG, "Loading face with index " + index + "from the DB");
        String [] whereArgs = { String.valueOf(index) };
        Face [] faces;
        if (database == null) {
            faces = loadFacesHelper(FACES_WHERE, whereArgs);
        } else {
            faces = loadFacesHelper(database, FACES_WHERE, whereArgs);
        }
        if (faces != null && faces.length > 0) {
            Log.v(DatabaseConstants.LOG_TAG, "Face retrieval successful");
            return faces[0];
        } else {
            Log.w(DatabaseConstants.LOG_TAG, "Problem while retrieving the face");
            return null;
        }
    }

    public Face[] loadAllFaces() {
        Log.v(DatabaseConstants.LOG_TAG, "Loading all the faces from the database");
        Face [] faces = loadFacesHelper(null, null);
        if (faces != null) {
            Log.v(DatabaseConstants.LOG_TAG, "Retrieved " + faces.length + " faces from the DB");
        } else {
            Log.w(DatabaseConstants.LOG_TAG, "Error when obtaining the faces from the database");
        }
        return faces != null ? faces : new Face[0];
    }

    private Face [] loadFacesHelper(String where, String[] whereArgs) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();

        Face [] faces = loadFacesHelper(database, where, whereArgs);
        database.close();
        return faces;
    }

    private Face [] loadFacesHelper(SQLiteDatabase database, String where, String[] whereArgs) {
        String [] columns = { DatabaseConstants.FACES_KEY, DatabaseConstants.FACES_INDEX,
                DatabaseConstants.FACES_BEAUTIFUL };
        Cursor cursor = database.query(DatabaseConstants.FACES_TABLE, columns, where,
                whereArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Face [] faces = new Face [cursor.getCount()];
            int keyIdx = cursor.getColumnIndex(DatabaseConstants.FACES_KEY);
            int indexIdx = cursor.getColumnIndex(DatabaseConstants.FACES_INDEX);
            int beautifulIdx = cursor.getColumnIndex(DatabaseConstants.FACES_BEAUTIFUL);

            int index = 0;
            do {
                faces[index] = new Face();
                faces[index].setKey(cursor.getString(keyIdx));
                faces[index].setIndex(cursor.getInt(indexIdx));
                if (!cursor.isNull(beautifulIdx)) {
                    faces[index].setBeautiful(cursor.getInt(beautifulIdx) == 1);
                }
                index++;
            } while (cursor.moveToNext());
            return faces;
        }
        return null;
    }
}
