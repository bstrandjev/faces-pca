package com.borisp.faces;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
    /** The format of the string which will be used to display the number of image */
    private static final String PAGE_NUMBER_FORMAT = "%d/%d";

    /** The index of the currently displayed element in the {@link #faces} array. */
    private int currentPictureIdx;
    /** An array storing all the faces from the database */
    private Face [] faces;
    /** Used to query the database */
    private DatabaseAdapter databaseAdapter;

    private OnClickListener nextOnClick = new OnClickListener() {
        public void onClick(View v) {
            Activity activity = MasterThesisAndroidActivity.this;
            RadioGroup radioGroup = (RadioGroup)activity.findViewById(R.id.radio_selection);
            int selected = radioGroup.getCheckedRadioButtonId();
            if (selected == -1) {
                showNothingSelectedError();
            } else {
                faces[currentPictureIdx].setBeautiful(selected == R.id.radio_beautiful);
                databaseAdapter.storeFace(faces[currentPictureIdx]);
                if ((++currentPictureIdx) < faces.length) {
                    loadPicture(currentPictureIdx);
                } else {
                    classificationFinishedMessage();
                    sendEmailWithResults();
                }
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

        int lastNotRated = -1;
        for (int i = 0; i < faces.length; i++) {
            if (faces[i].getBeautiful() == null) {
                lastNotRated = i;
                break;
            }
        }
        if (lastNotRated != -1) {
            this.currentPictureIdx = lastNotRated;
            loadPicture(lastNotRated);
        } else {
            this.currentPictureIdx = 0;
            showAllClassifiedMessage();
            loadPicture(0);
        }
        Button nextPictureBtn = (Button) findViewById(R.id.button_next_picture);
        nextPictureBtn.setOnClickListener(nextOnClick);
        Button prevPictureBtn = (Button) findViewById(R.id.button_prev_picture);
        prevPictureBtn.setOnClickListener(prevOnClick);
    }

    /** Sends an email containing in its body the results of the evaluation. */
    private void sendEmailWithResults() {
        JsonParser jsonParser = new JsonParser(this);
        String emailBody = jsonParser.serializeFaces(faces);

        Intent sendEmailntent = new Intent(Intent.ACTION_SENDTO);
        sendEmailntent.setType("text/html");
        sendEmailntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        sendEmailntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailBody));
        sendEmailntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.email_dialog_title));
        sendEmailntent.setData(Uri.parse("mailto:" + getString(R.string.email_address)));
        sendEmailntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(sendEmailntent);
    }

    private void loadPicture(int index) {
        faces[index].loadFaceImageView(this, R.id.image_sample);

        // Load the radio group accordingly
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_selection);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        radioGroup.clearCheck();
//        if (checkedId != -1) { //clearing the current radio button selection
//            ((RadioButton)radioGroup.findViewById(checkedId)).setChecked(false);
//        }
        if (faces[index].getBeautiful() != null) {
            if (faces[index].getBeautiful()) {
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
    private void showNothingSelectedError() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.no_selection_title);
        builder.setMessage(R.string.no_selection_message);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

    private void classificationFinishedMessage() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.final_title);
        builder.setMessage(R.string.final_message);
        builder.setPositiveButton(R.string.ok, null);
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