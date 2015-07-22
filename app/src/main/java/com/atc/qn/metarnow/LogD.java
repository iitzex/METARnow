package com.atc.qn.metarnow;

import android.util.Log;

public class LogD {
    boolean DEBUG = false;
    String TAG = "METARnow";
    public LogD(String msg) {
        if(DEBUG)
            Log.d(TAG, msg);
    }
}
