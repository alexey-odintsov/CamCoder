package com.alekso.camcoder.dialogs;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.alekso.camcoder.App;
import com.alekso.camcoder.CameraHelper;
import com.alekso.camcoder.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoResolutionDialog extends AppCompatDialog {

    private static final String TAG = "VideoResolutionDialog";
    private Button mButtonCancel;
    private ListView mListView;
    private String selectedValue;
    private VideoResolutionChangeListener veVideoResolutionChangeListener;

    public void setVideoResolutionChangeListener(VideoResolutionChangeListener veVideoResolutionChangeListener) {
        this.veVideoResolutionChangeListener = veVideoResolutionChangeListener;
    }

    public interface VideoResolutionChangeListener {
        void onVideoResolutionSelected(String selectedValue);
    }


    public VideoResolutionDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_video_resolution);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setTitle("Select video resolution");

        mListView = (ListView) findViewById(R.id.lvResolutions);
        mButtonCancel = (Button) findViewById(R.id.btnCancel);

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final List<String> resolutions = new ArrayList<>();
        Camera camera = CameraHelper.getDefaultCameraInstance();

        if (camera == null) return;

        List<Camera.Size> sizes = camera.getParameters().getSupportedVideoSizes();
        camera.release();
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
        mListView.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                resolutions));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = resolutions.get(position);
                ((App) getContext().getApplicationContext()).videoResolution = selectedValue;
                Log.d(TAG, "Resolution selected: " + selectedValue);
                veVideoResolutionChangeListener.onVideoResolutionSelected(selectedValue);
                dismiss();
            }
        });
    }
}
