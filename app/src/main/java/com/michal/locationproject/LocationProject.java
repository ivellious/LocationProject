package com.michal.locationproject;

import android.app.Application;
import android.util.Log;

/**
 * Created by michal on 11.11.17.
 */

public class LocationProject extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Location App"," Something went wrong", paramThrowable);
            }
        });
    }


    
}
