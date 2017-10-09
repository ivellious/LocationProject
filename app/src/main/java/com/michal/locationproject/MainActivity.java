package com.michal.locationproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION = 1;
    private static final String TAG = "MainActivity";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LAST_LOCATION_TIME = "last_location_time";
    public static final String MAIN_ACTIVITY_RECEIVER = "MAIN_ACTIVITY_RECEIVER";


    private EditText editText1, editText2;
    private TextView locationTextView;
    private TextView statusText;
    private Button sendJSONButton;
    private boolean isConnected;
    private SimpleLogger logger;

    private FusedLocationProviderClient fusedLocationProviderClient;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras().isEmpty()) {
                logger.log("extras are empty");
                showToast("Something went wrong while sending data");
                return;
            }

            if (intent.getExtras().getBoolean(ConnectivityChangeBroadcastReceiver.INTERNET_CONNECTION, false)) {
                updateConnectionStatus();
                return;
            }

            showToast("Response Code + " + intent.getExtras().getInt(PostJSONOkHttp.POST_RESPONSE_EXTRA));
        }
    };

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }

        updateConnectionStatus();

        this.registerReceiver(mBroadcastReceiver, new IntentFilter(MAIN_ACTIVITY_RECEIVER));
    }

    private void updateConnectionStatus() {
        if (new NetworkUtils(this).checkConnection()) {
            statusText.setText("Internet connection is ok");
            isConnected = true;
        } else {
            statusText.setText("Check your internet connection");
            isConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = new SimpleLogger(TAG);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
    }


    private void initViews() {
        statusText = (TextView) this.findViewById(R.id.status);
        locationTextView = (TextView) this.findViewById(R.id.currentLocationText);

        editText1 = (EditText) this.findViewById(R.id.jsonFirstParameter);
        editText2 = (EditText) this.findViewById(R.id.jsonSecondParameter);
        sendJSONButton = (Button) this.findViewById(R.id.sendJSONButton);

        sendJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // postJsonToServer();
                try {

                    if (!validateSource()) {
                        showToast("Fill out name and surname");
                        return;
                    }

                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare
                                    // situations this can be null.

                                    if (location != null) {
                                        try {
                                            JSONObject dataToSendJSONObject = obtainData();

                                            dataToSendJSONObject.accumulate(LATITUDE
                                                    , location.getLatitude());
                                            dataToSendJSONObject.accumulate(LONGITUDE
                                                    , location.getLongitude());
                                            dataToSendJSONObject.accumulate(LAST_LOCATION_TIME
                                                    , obtainLastKnownLocationTime(location));
                                            if (isConnected) {
                                                new PostJSONOkHttp(getApplicationContext())
                                                        .post("https://requestb.in/19xdfcv1"
                                                                , dataToSendJSONObject.toString());

                                            } else {
                                                showToast("No internet connection - failed to send");
                                            }
                                        } catch (JSONException e) {
                                            logger.log("Failed to obtain data", e);
                                            showToast("Failed to obtain data");
                                        }
                                        // Logic to handle location object
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        SimpleDateFormat simpleDateFormat =
                                                new SimpleDateFormat("MM-dd-yyy HH:mm");
                                        Date resultDate = new Date(location.getTime());

                                        locationTextView.setText("latitude: " + latitude
                                                + ", longitude: " + longitude
                                                + ", location time: "
                                                + simpleDateFormat.format(resultDate));
                                        logger.log("Latitude: "
                                                + latitude + ", longitude: "
                                                + longitude + ", location time "
                                                + simpleDateFormat.format(resultDate));
                                    }
                                }
                            });
                } catch (SecurityException e) {
                    logger.log("Check location permissions", e);
                    showToast("Check your location permissions for this application!");
                }


            }
        });
    }

    private String obtainLastKnownLocationTime(Location location) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MM-dd-yyy HH:mm");
        Date resultDate = new Date(location.getTime());
        return simpleDateFormat.format(resultDate);
    }

    private JSONObject obtainData() throws JSONException {
        JSONObject postData = new JSONObject();


        postData.accumulate(NAME, editText1.getText());
        postData.accumulate(SURNAME, editText2.getText());
        return postData;
    }

    private boolean validateSource() {
        return !TextUtils.isEmpty(editText1.getText()) && !TextUtils.isEmpty(editText2.getText());
    }
}
