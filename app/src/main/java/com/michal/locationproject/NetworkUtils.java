package com.michal.locationproject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by michal on 09.10.17.
 */

public class NetworkUtils {

    public static final String URL = "http://35.159.16.182/";

    private Context context;
    public NetworkUtils(Context context) {
        this.context = context;
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
