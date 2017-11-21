package com.michal.locationproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.michal.locationproject.NetworkUtils.URL;

/**
 * Created by michal on 14.11.17.
 */

public class LocationBroadcastReceiver extends BroadcastReceiver {


    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LAST_LOCATION_TIME = "lastLocationTime";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SimpleLogger simpleLogger;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "hey", Toast.LENGTH_LONG).show();
        simpleLogger = new SimpleLogger(LocationBroadcastReceiver.class.getSimpleName());
        simpleLogger.log("hey !!" + System.currentTimeMillis());
        reportLocation(context);
    }


    private void reportLocation(final Context context) {
        getFineLocation(context);

    }

    private JSONObject obtainData(Context context) throws JSONException {
        LocationAppSharedPreferences locationAppSharedPreferences = new LocationAppSharedPreferences(context);
        JSONObject postData = new JSONObject();
        postData.accumulate(NAME, locationAppSharedPreferences.getNameFromSharedPreferences());
        postData.accumulate(SURNAME, locationAppSharedPreferences.getNameFromSharedPreferences());
        //// TODO: 11.11.17 token!
        return postData;
    }

    private void getFineLocation(final Context context) {

        final int id = new LocationAppSharedPreferences(context).getToken();
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                try {

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
                                            , location.getTime());
                                    dataToSendJSONObject.accumulate(LATITUDE
                                            , location.getLatitude());
                                    dataToSendJSONObject.accumulate(LONGITUDE
                                            , location.getLongitude());

                                    simpleLogger.log(dataToSendJSONObject.toString());
                                    new PostJSONOkHttp(context, new Callback() {
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
        });
    }
}
