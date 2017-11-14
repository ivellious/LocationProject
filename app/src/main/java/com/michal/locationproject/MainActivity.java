package com.michal.locationproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION = 1;
    private static final String TAG = "MainActivity";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LAST_LOCATION_TIME = "last_location_time";
    public static final String MAIN_ACTIVITY_RECEIVER = "MAIN_ACTIVITY_RECEIVER";


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


        sendJSONButton = (Button) this.findViewById(R.id.sendJSONButton);

        sendJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    setLocationAlarm();
                } else {
                    showToast("Check internet connection");
                }
            }
        });
    }

    private void setLocationAlarm() {
        Intent locationIntent = new Intent(this, LocationBroadcastReceiver.class);
        PendingIntent locationPendingIntent = PendingIntent.getBroadcast(this, 132, locationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, locationPendingIntent);
    }


}
