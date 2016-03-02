package com.alekso.camcoder;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alekso.camcoder.dialogs.VideoResolutionDialog;

public class SettingsActivity extends AppCompatActivity {
    private App mApp;

    private Button mBtnSelectVideoResolution;
    private Button mBtnGetCamInfo;
    private TextView mTextViewVideoResolutionValue;
    private TextView mTextViewVideoPathValue;

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

        mTextViewVideoPathValue = (TextView) findViewById(R.id.tv_save_video_path_value);
        mTextViewVideoPathValue.setText(App.saveVideoPath);

        mBtnGetCamInfo = (Button) findViewById(R.id.btnGetCamInfo);
        mBtnGetCamInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                dialogBuilder.setTitle("Camera information");
                dialogBuilder.setView(getLayoutInflater().inflate(R.layout.camera_info, null));
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                Camera camera = CameraHelper.getDefaultCameraInstance();
                Camera.Parameters camParams = camera.getParameters();
                camera.release();

                final TextView mCameraParameters = (TextView) dialog.findViewById(R.id.tvCameraParams);
                if (camParams != null) {
                    String paramsString = camParams.flatten();
                    if (paramsString != null && !paramsString.isEmpty()) {
                        String[] params = paramsString.split(";");

                        StringBuilder sb = new StringBuilder("<b>Params:</b><br>");
                        for (String param : params) {
                            if (param != null) {
                                String[] paramKV = param.split("=");
                                sb.append("<b>").append(paramKV[0]).append("</b><br>");
                                String[] paramsValues = paramKV[1].split(",");
                                for (String value : paramsValues) {
                                    sb.append(" - ").append(value).append("<br>");
                                }
                            }
                        }

                        mCameraParameters.setText(Html.fromHtml(sb.toString()));
                    }
                }
            }
        });

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
