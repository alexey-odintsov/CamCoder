package com.alekso.camcoder;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private App mApp;

    private static final String TAG = "CamCoder";
    private Camera mCamera;
    private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private ImageButton mCaptureButton;
    private ImageButton mButtonSettings;
    private TextView mTimeLog;
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (App) getApplication();

        mPreview = (TextureView) findViewById(R.id.surface_view);
        mTimeLog = (TextView) findViewById(R.id.tvTimeLog);
        mCaptureButton = (ImageButton) findViewById(R.id.fab);
        mButtonSettings = (ImageButton) findViewById(R.id.btnSettings);

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    doStopRecord();
                } else {
                    doRecord();
                }
            }
        });

        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        doStopRecord();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mApp.loadSettings();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    private void doRecord() {
        Log.d(TAG, "start recording task");
        // BEGIN_INCLUDE(prepare_start_media_recorder)
        new MediaPrepareTask().execute(null, null, null);
        // END_INCLUDE(prepare_start_media_recorder)
    }

    private void doStopRecord() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        // BEGIN_INCLUDE(stop_release_media_recorder)
        // stop recording and release camera
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();  // stop the recording
        }
        releaseMediaRecorder(); // release the MediaRecorder object
        if (mCamera != null) {
            mCamera.lock();         // take camera access back from MediaRecorder
        }

        // inform the user that recording has stopped
        setCaptureButtonText(ACTION.CAPTURE);
        isRecording = false;
        releaseCamera();
        // END_INCLUDE(stop_release_media_recorder)
    }

    enum ACTION {
        CAPTURE,
        STOP
    }

    private void setCaptureButtonText(ACTION action) {
        switch (action) {
            case STOP:
                mCaptureButton.setImageResource(R.drawable.stop);
                mTimer = new CountDownTimer(App.recordLength, 1000) {

                    private int c = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        c++;
                        mTimeLog.setText(String.format("%02d:%02d", c / 60, c % 60));
                    }

                    @Override
                    public void onFinish() {
                        mTimeLog.setText("finish");
                    }
                }.start();
                break;
            case CAPTURE:
                mCaptureButton.setImageResource(R.drawable.play);

                break;
            default:
                mCaptureButton.setImageResource(R.drawable.play);
                break;
        }
    }

    private boolean prepareVideoRecorder() {
        Log.d(TAG, "prepareVideoRecorder started");
        // BEGIN_INCLUDE (configure_preview)
        mCamera = CameraHelper.getDefaultCameraInstance();

        Log.d(TAG, "found camera: " + mCamera);
        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();

        Camera.Size maxSize = mSupportedPreviewSizes.get(0);
        for (Camera.Size size : mSupportedPreviewSizes) {
            //Log.d(TAG, "sizes: " + size.width + "x" + size.height);
            if (maxSize.width < size.width) {
                maxSize = size;
            }
        }
        Camera.Size optimalSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());


        Log.d(TAG, "optimal size: " + optimalSize.width + "x" + optimalSize.height);
        Log.d(TAG, "Settings size: " + mApp.videoResolution);
        String size[] = mApp.videoResolution.split("x");
        // Use the Settings size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = Integer.valueOf(size[0]);
        profile.videoFrameHeight = Integer.valueOf(size[1]);

        //Log.d(TAG, "Profile: audio channels: " + profile.audioChannels);
        //Log.d(TAG, "Profile: audio bit rate: " + profile.audioBitRate);
        //Log.d(TAG, "Profile: audio sample rate: " + profile.audioSampleRate);
        //Log.d(TAG, "Profile: audio codec: " + profile.audioCodec);

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        // END_INCLUDE (configure_preview)


        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO).toString());
        // END_INCLUDE (configure_media_recorder)

        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.d(TAG, "MR INFO: " + what + "; extra: " + extra);
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    // restartVideo()
                    Log.d(TAG, "Max duration reached");
                } else if (what == 1000 /*MediaRecorder.MEDIA_RECORDER_TRACK_INFO_COMPLETION_STATUS*/) {
                    Log.d(TAG, "Restart video recording");
                    doStopRecord();

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            doRecord();
                        }
                    }, App.delayBetweenRecord);

                }
            }
        });
        mMediaRecorder.setMaxDuration(App.recordLength);

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        Log.d(TAG, "prepareVideoRecorder finished");
        return true;
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                Log.d(TAG, "mediaRecorder.start");
                mMediaRecorder.start();
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                MainActivity.this.finish();
            }
            // inform the user that recording has started
            setCaptureButtonText(ACTION.STOP);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
