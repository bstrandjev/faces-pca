package com.borisp.faces;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MasterThesisAndroidActivity extends Activity {
    private int currentPictureIdx;
    private OnClickListener btnOnClick = new OnClickListener() {
        public void onClick(View v) {
            Activity activity = MasterThesisAndroidActivity.this;
            RadioGroup radioGroup = (RadioGroup)activity.findViewById(R.id.radio_selection);
            int selected = radioGroup.getCheckedRadioButtonId();
            if (selected == -1) {
                Builder builder = new AlertDialog.Builder(activity);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle(R.string.no_selection_title);
                builder.setMessage(R.string.no_selection_message);
                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            } else if(!loadNextPicture()) {
                Builder builder = new AlertDialog.Builder(activity);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle(R.string.final_title);
                builder.setMessage(R.string.final_message);
                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        currentPictureIdx = 0;
        loadNextPicture();
        Button nextPictureBtn = (Button) findViewById(R.id.button_next_picture);
        nextPictureBtn.setOnClickListener(btnOnClick);
    }

    private boolean loadNextPicture() {
        currentPictureIdx++;
        String currentPictureName = "img" + currentPictureIdx;
        int id =
                getResources().getIdentifier(currentPictureName, "drawable", this.getPackageName());
        if (id == 0) {
            return false;
        } else {
            RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_selection);
            int selected = radioGroup.getCheckedRadioButtonId();
            if (selected != -1) {
                RadioButton radioButton = (RadioButton)radioGroup.getChildAt(selected);
                radioButton.setChecked(false);
            }
            ImageView image = (ImageView)findViewById(R.id.image_sample);
            image.setImageResource(id);
            return true;
        }
    }
}