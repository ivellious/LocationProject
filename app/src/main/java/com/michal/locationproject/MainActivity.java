package com.michal.locationproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION = 1;
    private static final int PENDING_INTENT_LOCATION_CODE = 1123;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MAIN_ACTIVITY_RECEIVER = "MAIN_ACTIVITY_RECEIVER";
    PendingIntent locationPendingIntent;

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
                return;
            }

            if (intent.getExtras().getBoolean(ConnectivityChangeBroadcastReceiver.INTERNET_CONNECTION, false)) {
                updateConnectionStatus();
            }
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
        sendJSONButton = (Button) this.findViewById(R.id.sendJSONButton);
        sendJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    checkLocationSettings();
                } else {
                    showToast("Check internet connection");
                }
            }
        });
    }

    private void checkLocationSettings() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(java.util.concurrent.TimeUnit.SECONDS.toMillis(20));
        locationRequest.setFastestInterval(java.util.concurrent.TimeUnit.SECONDS.toMillis(10));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Intent locationIntent = new Intent(getApplicationContext(), LocationBroadcastReceiver.class);
                locationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), PENDING_INTENT_LOCATION_CODE, locationIntent, 0);

                try {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationPendingIntent);
                } catch (SecurityException ex) {
                    logger.log("Security ex",ex);
                }
            }
        });
    }
}
