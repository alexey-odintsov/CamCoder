package com.alekso.camcoder;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;


public class App extends Application {
    private static final String TAG = "App";
    /*package*/ static final String PREFS_NAME = "cam_coder_settings";

    public enum RECORD_LENGTH {
        _1(60 * 1000, "1m"),
        _2(2 * 60 * 1000, "2m"),
        _3(3 * 60 * 1000, "3m"),
        _5(5 * 60 * 1000, "5m"),
        _7(7 * 60 * 1000, "7m"),
        _10(10 * 60 * 1000, "10m");

        private int mDuration;
        private String mName;

        RECORD_LENGTH(int duration, String name) {
            mDuration = duration;
            mName = name;
        }

        public int getDuration() {
            return mDuration;
        }

        public String getName() {
            return mName;
        }

        public static RECORD_LENGTH getByDuration(int duration) {
            for (RECORD_LENGTH rl : values()) {
                if (rl.getDuration() == duration) return rl;
            }

            return null;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public enum DELAY_BETWEEN_RECORD {
        _50(50, "50ms"),
        _100(100, "100ms"),
        _200(200, "200ms"),
        _300(300, "300ms"),
        _400(400, "400ms"),
        _500(500, "500ms");

        private int mDelay;
        private String mName;

        DELAY_BETWEEN_RECORD(int delay, String name) {
            mDelay = delay;
            mName = name;
        }

        public int getDelay() {
            return mDelay;
        }

        public String getName() {
            return mName;
        }

        public static DELAY_BETWEEN_RECORD getByDelay(int delay) {
            for (DELAY_BETWEEN_RECORD rl : values()) {
                if (rl.getDelay() == delay) return rl;
            }

            return null;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private static final String DEFAULT_SAVE_VIDEO_PATH = "/";
    private static final String DEFAULT_VIDEO_RESOLUTION = "640x480";
    private static final int DEFAULT_RECORD_LENGTH = RECORD_LENGTH._3.getDuration();
    private static final int DEFAULT_DELAY_BETWEEN_RECORD = DELAY_BETWEEN_RECORD._200.getDelay();

    private static final String SETTINGS_SAVE_VIDEO_PATH = "save_video_path";
    private static final String SETTINGS_VIDEO_RESOLUTION = "video_resolution";
    private static final String SETTINGS_RECORD_LENGTH = "video_length";
    private static final String SETTINGS_DELAY_BETWEEN_RECORDS = "video_delay";

    public static String saveVideoPath = DEFAULT_SAVE_VIDEO_PATH;
    public static String videoResolution = DEFAULT_VIDEO_RESOLUTION;
    public static int recordLength = DEFAULT_RECORD_LENGTH;
    public static int delayBetweenRecord = DEFAULT_DELAY_BETWEEN_RECORD;

    /**
     * Load app settings
     */
    public void loadSettings() {
        Log.d(TAG, "load app settings");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        saveVideoPath = settings.getString(SETTINGS_SAVE_VIDEO_PATH, DEFAULT_SAVE_VIDEO_PATH);
        videoResolution = settings.getString(SETTINGS_VIDEO_RESOLUTION, DEFAULT_VIDEO_RESOLUTION);
        recordLength = settings.getInt(SETTINGS_RECORD_LENGTH, DEFAULT_RECORD_LENGTH);
        delayBetweenRecord = settings.getInt(SETTINGS_DELAY_BETWEEN_RECORDS, DEFAULT_DELAY_BETWEEN_RECORD);
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
        editor.putInt(SETTINGS_RECORD_LENGTH, recordLength);
        editor.putInt(SETTINGS_DELAY_BETWEEN_RECORDS, delayBetweenRecord);

        editor.apply();
    }
}
