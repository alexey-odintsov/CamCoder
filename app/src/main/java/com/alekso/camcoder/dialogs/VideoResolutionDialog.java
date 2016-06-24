package com.alekso.camcoder.dialogs;

import android.app.DialogFragment;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alekso.camcoder.CameraHelper;
import com.alekso.camcoder.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoResolutionDialog extends DialogFragment {

    private static final String TAG = "VideoResolutionDialog";
    private Button mButtonCancel;
    private ListView mListView;
    private VideoResolutionChangeListener listener;
    private ProgressBar mProgressBar;
    private TextView mProgressLabel;

    public void setVideoResolutionChangeListener(VideoResolutionChangeListener veVideoResolutionChangeListener) {
        this.listener = veVideoResolutionChangeListener;
    }

    public interface VideoResolutionChangeListener {
        void onVideoResolutionSelected(String selectedValue);
    }

    public static VideoResolutionDialog newInstance() {
        VideoResolutionDialog dialog = new VideoResolutionDialog();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Select video resolution");
        View v = inflater.inflate(R.layout.dialog_video_resolution, container, false);

        mListView = (ListView) v.findViewById(R.id.lvResolutions);
        mButtonCancel = (Button) v.findViewById(R.id.btnCancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mProgressLabel = (TextView) v.findViewById(R.id.progressLabel);

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        GetCameraSizes getCameraSizesTask = new GetCameraSizes();
        getCameraSizesTask.execute();
        return v;
    }

    class GetCameraSizes extends AsyncTask<Void, Void, List<Camera.Size>> {

        @Override
        protected List<Camera.Size> doInBackground(Void... params) {
            Camera camera = CameraHelper.getDefaultCameraInstance();

            if (camera == null) return null;

            List<Camera.Size> sizes = camera.getParameters().getSupportedVideoSizes();
            camera.release();
            return sizes;
        }

        @Override
        protected void onPostExecute(List<Camera.Size> sizes) {
            final List<String> resolutions = new ArrayList<>();
            Collections.sort(sizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size a, Camera.Size b) {
                    if (a.width > b.width) {
                        return -1;
                    } else if (a.height > b.height) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (Camera.Size size : sizes) {
                resolutions.add(String.format("%dx%d", size.width, size.height));
                Log.d(TAG, String.format("%dx%d", size.width, size.height));
            }
            if (getDialog() != null && getDialog().isShowing()) {
                mListView.setAdapter(new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        resolutions));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listener.onVideoResolutionSelected(resolutions.get(position));
                        dismiss();
                    }
                });
                mProgressBar.setVisibility(View.GONE);
                mProgressLabel.setVisibility(View.GONE);
            }
        }
    }
}
