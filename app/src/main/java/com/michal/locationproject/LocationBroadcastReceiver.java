package com.michal.locationproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.michal.locationproject.NetworkUtils.URL;

/**
 * Created by michal on 14.11.17.
 */

public class LocationBroadcastReceiver extends BroadcastReceiver {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LAST_LOCATION_TIME = "lastLocationTime";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SimpleLogger simpleLogger;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "obtaining location", Toast.LENGTH_LONG).show();
        simpleLogger = new SimpleLogger(LocationBroadcastReceiver.class.getSimpleName());
        simpleLogger.log("hey !!" + System.currentTimeMillis());
        reportLocation(context);
    }

    private void reportLocation(final Context context) {
        getFineLocation(context);

    }

    private void getFineLocation(Context context) {
        try {
            final int id = new LocationAppSharedPreferences(context).getToken();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("MM-dd-yyy HH:mm");
                        Date resultDate = new Date(location.getTime());

                        simpleLogger.log("Latitude: "
                                + latitude + ", longitude: "
                                + longitude + ", location time "
                                + simpleDateFormat.format(resultDate));

                        try {
                            JSONObject dataToSendJSONObject = new JSONObject();

                            dataToSendJSONObject.accumulate(LAST_LOCATION_TIME
                                    , TimeUnit.MILLISECONDS.toSeconds(location.getTime()));
                            dataToSendJSONObject.accumulate(LATITUDE
                                    , location.getLatitude());
                            dataToSendJSONObject.accumulate(LONGITUDE
                                    , location.getLongitude());

                            simpleLogger.log(dataToSendJSONObject.toString());
                            new PostJSONOkHttp(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    simpleLogger.log("failure !!");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    simpleLogger.log("response code is " + response.code());
                                }
                            })
                                    .post(URL+"location/personId="+id
                                            , dataToSendJSONObject.toString());
                        } catch (JSONException e) {
                            simpleLogger.log("Failed to obtain data", e);
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            simpleLogger.log("Check location permissions", e);
        }
    }
}
