package com.alekso.camcoder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alekso.camcoder.dialogs.VideoResolutionDialog;

public class SettingsActivity extends AppCompatActivity {
    private App mApp;

    private Button mBtnSelectVideoResolution;
    private TextView mTextViewVideoResolutionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mApp = (App) getApplication();

        mBtnSelectVideoResolution = (Button) findViewById(R.id.btn_video_resolution_change);
        mBtnSelectVideoResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoResolutionDialog dialog = new VideoResolutionDialog(SettingsActivity.this);
                dialog.setVideoResolutionChangeListener(new VideoResolutionDialog.VideoResolutionChangeListener() {
                    @Override
                    public void onVideoResolutionSelected(String selectedValue) {
                        mTextViewVideoResolutionValue.setText(selectedValue);
                    }
                });
                dialog.show();
            }
        });

        mTextViewVideoResolutionValue = (TextView) findViewById(R.id.tv_video_resolution_value);
        mTextViewVideoResolutionValue.setText(App.videoResolution);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApp.saveSettings();
    }
}
