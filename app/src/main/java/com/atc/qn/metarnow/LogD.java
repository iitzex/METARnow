package com.atc.qn.metarnow;

import android.util.Log;

public class LogD {
//    static boolean DEBUG = true;
    static boolean DEBUG = false;
    static String TAG = "METARnow";

    static public void out(String msg) {
        if(DEBUG)
            Log.d(TAG, msg);
    }

    static public void out(int msg) {
        if(DEBUG)
            Log.d(TAG, String.valueOf(msg));
    }
}
