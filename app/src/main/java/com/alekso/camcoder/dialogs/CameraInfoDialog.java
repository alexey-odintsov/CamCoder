package com.alekso.camcoder.dialogs;

import android.app.DialogFragment;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alekso.camcoder.CameraHelper;
import com.alekso.camcoder.R;

/**
 * Created by alekso on 24/06/16.
 */
public class CameraInfoDialog extends DialogFragment {
    TextView mCameraParameters;
    ProgressBar mProgressBar;

    public static CameraInfoDialog newInstance() {
        CameraInfoDialog dialog = new CameraInfoDialog();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Select category");

        View v = inflater.inflate(R.layout.camera_info, container, false);
        mCameraParameters = (TextView) v.findViewById(R.id.tvCameraParams);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        Button btnClose = (Button) v.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("Camera information");

        GetCameraInfoTask getCameraInfoTask = new GetCameraInfoTask();
        getCameraInfoTask.execute();
        return v;
    }

    class GetCameraInfoTask extends AsyncTask<Void, Void, Camera.Parameters> {

        @Override
        protected Camera.Parameters doInBackground(Void... params) {
            Camera.Parameters camParams = null;
            Camera camera = CameraHelper.getDefaultCameraInstance();
            if (camera != null) {
                camParams = camera.getParameters();
                camera.release();
            }
            return camParams;
        }

        @Override
        protected void onPostExecute(Camera.Parameters params) {
            if (params != null) {
                String paramsString = params.flatten();
                if (paramsString != null && !paramsString.isEmpty()) {
                    String[] paramsArr = paramsString.split(";");

                    StringBuilder sb = new StringBuilder("<b>Params:</b><br>");
                    for (String param : paramsArr) {
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

            mProgressBar.setVisibility(View.GONE);
        }
    }
}
