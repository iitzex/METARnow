package com.atc.qn.metarnow;

import android.util.Log;

public class LogD {
    static boolean DEBUG = true;
    static String TAG = "METARnow";

    static public void print(String msg) {
        if(DEBUG)
            Log.d(TAG, msg);
    }

    static public void print(int msg) {
        if(DEBUG)
            Log.d(TAG, String.valueOf(msg));
    }
}
