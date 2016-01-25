package com.alekso.camcoder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alekso.camcoder.dialogs.VideoResolutionDialog;

public class SettingsActivity extends AppCompatActivity {
    private App mApp;

    private Button mBtnSelectVideoResolution;

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
                dialog.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApp.saveSettings();
    }
}
