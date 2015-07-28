package com.atc.qn.metarnow;

import android.util.Log;

public class LogD {
    static boolean DEBUG = false;
    static String TAG = "METARnow";

    static public void print(String msg) {
        if(DEBUG)
            Log.d(TAG, msg);
    }
}
