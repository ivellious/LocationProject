package com.michal.locationproject;

import android.util.Log;

/**
 * Created by michal on 05.10.17.
 */

public class SimpleLogger {

    private String tag;

    public SimpleLogger(String tag) {
        this.tag = tag;
    }

    public void log(String message) {
        Log.e(tag, message);
    }

    public void log(String message, Throwable t) {
        Log.e(tag, message, t);
    }
}
