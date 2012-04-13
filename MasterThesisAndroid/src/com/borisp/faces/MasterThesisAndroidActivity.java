package com.borisp.faces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.borisp.faces.beans.Face;
import com.borisp.faces.database.DatabaseAdapter;
import com.borisp.faces.database.DatabaseAdapterImpl;
import com.borisp.faces.helpers.JsonParser;

/**
 * The only activity of the application.
 *
 * It shows the pictures one by one and provides radio buttons with which each picture can be
 * classified.
 *
 * @author Boris
 */
public class MasterThesisAndroidActivity extends Activity {
    private static final String TAG = "MasterThesisAndroidActivity";

    /** This number is the seed with which we will make the random shuffle. */
    private static final int RANDOM_SHUFFLE_NUMBER = 100003;
    /** The format of the string which will be used to display the number of image */
    private static final String PAGE_NUMBER_FORMAT = "%d/%d";
    /** The path to which classification json is temporarily stored before attached to email. */
    private static final String CLASSIFICATION_JSON_TEMP_FILE = "classification.json";

    /** The index of the currently displayed element in the {@link #faces} array. */
    private int currentPictureIdx;
    /** An array storing all the faces from the database */
    private Face [] faces;
    /** The images are randomly shuffled. */
    private int [] indices;

    /** Used to query the database */
    private DatabaseAdapter databaseAdapter;

    private OnClickListener checkListener = new OnClickListener() {
        public void onClick(View v) {
            Button nextButton = (Button) findViewById(R.id.button_next_picture);
            nextButton.setEnabled(true);
            nextButton.setFocusable(true);

            // Load the next picture when radio button is pressed
            loadNextPicture();
        }
    };
    private OnClickListener nextOnClick = new OnClickListener() {
        public void onClick(View v) {
            if (!loadNextPicture()) {
                classificationFinishedMessage();
            }
        }
    };

    private OnClickListener prevOnClick = new OnClickListener() {
        public void onClick(View v) {
            loadPicture(--currentPictureIdx);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.databaseAdapter = new DatabaseAdapterImpl(this);
        this.faces = databaseAdapter.loadAllFaces();
        this.indices = new int[faces.length];

        for (int i = 0; i < faces.length; i++) {
            indices[i] = (i * RANDOM_SHUFFLE_NUMBER) % faces.length;
        }
        int lastNotRated = -1;
        for (int i = 0; i < faces.length; i++) {
            if (faces[indices[i]].getBeautiful() == null) {
                lastNotRated = i;
                break;
            }
        }
        if (lastNotRated != -1) {
            this.currentPictureIdx = lastNotRated;
        } else {
            this.currentPictureIdx = faces.length - 1;
            showAllClassifiedMessage();
        }
        loadPicture(currentPictureIdx);

        findViewById(R.id.button_next_picture).setOnClickListener(nextOnClick);
        findViewById(R.id.button_prev_picture).setOnClickListener(prevOnClick);
        findViewById(R.id.radio_beautiful).setOnClickListener(checkListener);
        findViewById(R.id.radio_not_beautiful).setOnClickListener(checkListener);
    }

    /** Sends an email containing in its body the results of the evaluation. */
    private void sendEmailWithResults() {
        Uri attachmentTextUri = Uri.fromFile(createFacesJsonFile());
        Intent sendEmailntent = new Intent(Intent.ACTION_SEND);
        sendEmailntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        sendEmailntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));

        Log.d(TAG, "Sending email with attachment " + attachmentTextUri);
        sendEmailntent.setType("text/message");
        sendEmailntent.putExtra(Intent.EXTRA_STREAM, attachmentTextUri);
        sendEmailntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.email_dialog_title));
        sendEmailntent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { getString(R.string.email_address) });
        startActivity(Intent.createChooser(sendEmailntent,
                getString(R.string.email_intent_chooser)));
    }

    /** Creates the File storing the classification json temporarily stored in the file system. */
    private File createFacesJsonFile() {
        JsonParser jsonParser = new JsonParser(this);
        String attachmentText = jsonParser.serializeFaces(faces);

        File jsonOutFile = getFacesJsonFile();
        try {
            FileOutputStream jsonOut = new FileOutputStream(jsonOutFile);
            jsonOut.write(attachmentText.getBytes());
            jsonOut.flush();
            jsonOut.close();
            Log.d(TAG, "The attachment file created. Size is: " + jsonOutFile.length());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "The classification json file could not be created", e);
        } catch (IOException e) {
            Log.e(TAG, "Error while writing to the classification json", e);
        }
        return jsonOutFile;
    }

    @Override
    public void onBackPressed() {
        File classificationFile = getFacesJsonFile();
        if (classificationFile != null) {
            Log.i(TAG, "Deleting the classification file");
            classificationFile.delete();
        }
        finish();
        super.onBackPressed();
    }

    /** Return the path to which the json formatted classification will be stored temporarily. */
    private File getFacesJsonFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                CLASSIFICATION_JSON_TEMP_FILE);
    }

    /**
     * Loads the next picture in the list.
     *
     * @return True if there is such, false otherwise.
     */
    private boolean loadNextPicture() {
        RadioGroup radioGroup = (RadioGroup)this.findViewById(R.id.radio_selection);
        int selected = radioGroup.getCheckedRadioButtonId();
        faces[indices[currentPictureIdx]].setBeautiful(selected == R.id.radio_beautiful);
        databaseAdapter.storeFace(faces[indices[currentPictureIdx]]);
        if (currentPictureIdx + 1 < faces.length) {
            currentPictureIdx++;
            loadPicture(currentPictureIdx);
            return true;
        }
        return false;
    }

    /**
     * Displays the picture with the given index. Updates all the inputs on the screen accordingly.
     *
     * @param index The index in the faces array of the picture to display.
     */
    private void loadPicture(int index) {
        faces[indices[index]].loadFaceImageView(this, R.id.image_sample);

        // Load the radio group accordingly
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_selection);
        radioGroup.clearCheck();

        Button nextButton = (Button) findViewById(R.id.button_next_picture);
        nextButton.setEnabled(false);
        nextButton.setFocusable(false);
        if (faces[indices[index]].getBeautiful() != null) {
            nextButton.setEnabled(true);
            nextButton.setFocusable(true);
            if (faces[indices[index]].getBeautiful()) {
                ((RadioButton)radioGroup.findViewById(R.id.radio_beautiful)).setChecked(true);
            } else {
                ((RadioButton)radioGroup.findViewById(R.id.radio_not_beautiful)).setChecked(true);
            }
        }

        // Load the image number accordingly
        TextView imageNumberTextView = (TextView)findViewById(R.id.image_number_text);
        imageNumberTextView.setText(String.format(PAGE_NUMBER_FORMAT, index + 1, faces.length));

        // Enable / disable the previous button
        Button prevButton = (Button) findViewById(R.id.button_prev_picture);
        if (currentPictureIdx == 0) {
            prevButton.setEnabled(false);
            prevButton.setFocusable(false);
        } else {
            prevButton.setEnabled(true);
            prevButton.setFocusable(true);
        }
    }

    // Methods for alert dialogs
    private void classificationFinishedMessage() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.final_title);
        builder.setMessage(R.string.final_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendEmailWithResults();
            }
        });
        builder.show();
    }

    private void showAllClassifiedMessage() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.all_classified_title);
        builder.setMessage(R.string.all_classified_message);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }
}