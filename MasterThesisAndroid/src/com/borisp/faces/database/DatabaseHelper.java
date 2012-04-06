package com.borisp.faces.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.borisp.faces.helpers.IOHelper;

/**
 * Class responsible for keeping the database up-to date.
 * <p>
 * We use update scripts to migrate the schema to every new version. The scripts for a particular
 * version are included in 'database/version<version_num>' dir. The version number is controlled by
 * {@link DatabaseHelper#DATABASE_VERSION}. Every time this number is increased the database will be
 * updated correspondingly.
 * <p>
 * Format of the update script directories:
 * <ul>
 * <li>Every such directory contains a file listing all the scripts to be executed. The name of the
 * file should always be the same as {@link DatabaseHelper#VERSION_UPDATE_SCRIPTS_LIST_FILE}. In
 * this file list the name of the sequence of update files to be executed. Lines beginning with '#'
 * and blank lines are ignored while processing the file.</li>
 * <li>Include the update scripts in the corresponding folder. The names should be the same as
 * listed in the script list file. Every file should contain valid SQLite script consisting of a
 * single command and not include commit commands (commit is executed programmatically).</li>
 * </ul>
 *
 * @author boris.strandjev
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database constants
    private static int DATABASE_VERSION = 1;
    // Update scripts constants
    private static final String VERSION_UPDATE_SCRIPTS_DIR = "database/version_%02d/";
    private static final String VERSION_UPDATE_SCRIPTS_LIST_FILE = "script_list.txt";

    /** Update scripts are stored in the assets directory. */
    protected AssetManager assets;
    private boolean foreignKeysEnabled;

    public DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DATABASE_VERSION);
        this.assets = context.getAssets();
        this.foreignKeysEnabled = false;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(DatabaseConstants.LOG_TAG, "Creating the database");
        onUpgrade(database, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int fromVersion, int toVersion) {
        Log.i(DatabaseConstants.LOG_TAG, "Upgrading the database from version " + fromVersion
                + " to version " + toVersion);
        for (int version = fromVersion + 1; version <= toVersion; ++version) {
            Log.v(DatabaseConstants.LOG_TAG, "Upgrading to version " + version);
            List<String> scriptFileNames = getScriptFileNames(version);
            for (String scriptFileName : scriptFileNames) {
                Log.v(DatabaseConstants.LOG_TAG, "Executing upgrade script: " + scriptFileName);
                String sqlString = IOHelper.getFileContent(assets, scriptFileName);
                database.execSQL(sqlString);
            }
        }
    }

    /**
     * Gets the file names of all update scripts of a DB version.
     *
     * @param version The version for which to fetch the scripts.
     * @return The file names of all the update scripts of the DB version.
     */
    protected List<String> getScriptFileNames(int version) {
        List<String> scriptFileNames = new ArrayList<String>();
        String versionDir = String.format(VERSION_UPDATE_SCRIPTS_DIR, version);
        String scriptListFileName = versionDir + VERSION_UPDATE_SCRIPTS_LIST_FILE;
        String versionScriptListContent = IOHelper.getFileContent(assets, scriptListFileName);
        if (versionScriptListContent != null) {
            String[] lines = versionScriptListContent.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.length() != 0 && !line.startsWith("#")) {
                    scriptFileNames.add(versionDir + line);
                }
            }
        } else {
            Log.w(DatabaseConstants.LOG_TAG, "No update script list found for version " + version);
        }
        return scriptFileNames;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly() && !foreignKeysEnabled) {
            // Enable foreign key constraints
            Log.d(DatabaseConstants.LOG_TAG, "enable foreign keys");
            db.execSQL("PRAGMA foreign_keys=ON;");
            foreignKeysEnabled = true;
        }
    }
}

