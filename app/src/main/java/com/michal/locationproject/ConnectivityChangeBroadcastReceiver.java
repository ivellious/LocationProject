package com.michal.locationproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by michal on 09.10.17.
 */

public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver {


    public static final String INTERNET_CONNECTION = "INTERNET_CONNECTION";

    @Override
    public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent(MainActivity.MAIN_ACTIVITY_RECEIVER).putExtra(INTERNET_CONNECTION, true));
    }
}
