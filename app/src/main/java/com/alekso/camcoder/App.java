package com.alekso.camcoder;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;


public class App extends Application {
    private static final String TAG = "App";
    /*package*/ static final String PREFS_NAME = "cam_coder_settings";

    private static final String DEFAULT_SAVE_VIDEO_PATH = "/";
    private static final String DEFAULT_VIDEO_RESOLUTION = "640x480";
    private static final String SETTINGS_SAVE_VIDEO_PATH = "save_video_path";
    private static final String SETTINGS_VIDEO_RESOLUTION = "video_resolution";

    public String saveVideoPath = DEFAULT_SAVE_VIDEO_PATH;
    public String videoResolution = DEFAULT_VIDEO_RESOLUTION;

    /**
     * Load app settings
     */
    public void loadSettings() {
        Log.d(TAG, "load app settings");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        saveVideoPath = settings.getString(SETTINGS_SAVE_VIDEO_PATH, DEFAULT_SAVE_VIDEO_PATH);
        videoResolution = settings.getString(SETTINGS_VIDEO_RESOLUTION, DEFAULT_VIDEO_RESOLUTION);
    }

    /**
     * Save app settings
     */
    void saveSettings() {
        Log.d(TAG, "save app settings");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(SETTINGS_SAVE_VIDEO_PATH, saveVideoPath);
        editor.putString(SETTINGS_VIDEO_RESOLUTION, videoResolution);

        editor.apply();
    }
}