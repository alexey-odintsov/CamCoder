package com.alekso.camcoder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alekso.camcoder.dialogs.DirectoryChooserDialog;
import com.alekso.camcoder.dialogs.VideoResolutionDialog;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "Settings";
    private App mApp;

    private Button mBtnSelectVideoPath;
    private Button mBtnSelectVideoResolution;
    private Button mBtnGetCamInfo;
    private Button mBtnChangeRecordLength;
    private Button mBtnChangeDelayBetweenRecord;
    private TextView mTextViewVideoResolutionValue;
    private TextView mTextViewVideoPathValue;
    private TextView mTextViewRecordLength;
    private TextView mTextViewDelayBetweenRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mApp = (App) getApplication();

        mBtnSelectVideoPath = (Button) findViewById(R.id.btn_save_video_path_change);
        mBtnSelectVideoPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DirectoryChooserDialog dialog = DirectoryChooserDialog.newInstance(App.saveVideoPath, new DirectoryChooserDialog.OnDirectorySelectListener() {
                    @Override
                    public void onDirectorySelect(String path) {
                        App.saveVideoPath = path;
                        mTextViewVideoPathValue.setText(App.saveVideoPath);
                    }
                });
                dialog.show(getSupportFragmentManager(), "SelectDirectoryDialog");

            }
        });

        mBtnSelectVideoResolution = (Button) findViewById(R.id.btn_video_resolution_change);
        mBtnSelectVideoResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoResolutionDialog dialog = new VideoResolutionDialog(SettingsActivity.this);
                dialog.setVideoResolutionChangeListener(new VideoResolutionDialog.VideoResolutionChangeListener() {
                    @Override
                    public void onVideoResolutionSelected(String selectedValue) {
                        App.videoResolution = selectedValue;
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

        mBtnChangeRecordLength = (Button) findViewById(R.id.btn_change_record_length);
        mBtnChangeRecordLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        if (which == Dialog.BUTTON_POSITIVE) {
                            App.RECORD_LENGTH len = (App.RECORD_LENGTH) (lv.getItemAtPosition(lv.getCheckedItemPosition()));
                            App.recordLength = len.getDuration();
                            mTextViewRecordLength.setText(len.getName());
                            dialog.dismiss();
                        }
                    }
                };

                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle("Select Record length");
                ArrayAdapter<App.RECORD_LENGTH> adapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.select_dialog_singlechoice, App.RECORD_LENGTH.values());
                adb.setSingleChoiceItems(adapter, -1, myClickListener);
                adb.setPositiveButton("Ok", myClickListener);
                AlertDialog dialog = adb.create();
                dialog.show();
            }
        });

        mTextViewRecordLength = (TextView) findViewById(R.id.tv_record_length_value);
        App.RECORD_LENGTH rl = App.RECORD_LENGTH.getByDuration(App.recordLength);
        mTextViewRecordLength.setText(rl.getName());

        mBtnChangeDelayBetweenRecord = (Button) findViewById(R.id.btn_change_delay_between_records);
        mBtnChangeDelayBetweenRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        if (which == Dialog.BUTTON_POSITIVE) {
                            App.DELAY_BETWEEN_RECORD delay = (App.DELAY_BETWEEN_RECORD) (lv.getItemAtPosition(lv.getCheckedItemPosition()));
                            App.delayBetweenRecord = delay.getDelay();
                            mTextViewDelayBetweenRecord.setText(delay.getName());
                            dialog.dismiss();
                        }
                    }
                };

                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle("Select Delay value");
                ArrayAdapter<App.DELAY_BETWEEN_RECORD> adapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.select_dialog_singlechoice, App.DELAY_BETWEEN_RECORD.values());
                adb.setSingleChoiceItems(adapter, -1, myClickListener);
                adb.setPositiveButton("Ok", myClickListener);
                AlertDialog dialog = adb.create();
                dialog.show();
            }
        });

        mTextViewDelayBetweenRecord = (TextView) findViewById(R.id.tv_delay_between_records_value);
        App.DELAY_BETWEEN_RECORD dbr = App.DELAY_BETWEEN_RECORD.getByDelay(App.delayBetweenRecord);
        mTextViewDelayBetweenRecord.setText(dbr.getName());

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
                if (camera == null) return;
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
                                if (paramKV.length > 1 && paramKV[1] != null) {
                                    String[] paramsValues = paramKV[1].split(",");
                                    for (String value : paramsValues) {
                                        sb.append(" - ").append(value).append("<br>");
                                    }
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
