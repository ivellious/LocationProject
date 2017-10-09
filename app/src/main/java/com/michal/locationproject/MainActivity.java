package com.michal.locationproject;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private static final String TAG = "MainActivity";
    private static final int COARSE_LOCATION_PERMISSION = 1;
    private static final int FINE_LOCATION_PERSMISSION = 2;

    private EditText editText1, editText2, editText3;
    private TextView locationTextView;
    private TextView statusText;
    private Button sendJSONButton;
    private boolean isConnected;
    private SimpleLogger logger;

    private FusedLocationProviderClient fusedLocationProviderClient;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                logger.log("intent is null");
                return;
            }

            if (intent.getExtras().isEmpty()) {
                logger.log("extras are empty");
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
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_PERMISSION);
//            showToast("Check location permission for this app");
        }


        checkConnection();
        this.registerReceiver(mBroadcastReceiver, new IntentFilter(PostJSONOkHttp.POST_ACTION));
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


//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_PERMISSION);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            showToast("Check location permission for this app");
//        }


    }

    private void checkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            statusText.setText("Internet connection is ok");
            isConnected = true;
        } else {
            statusText.setText("Check your internet connection");
            isConnected = false;
        }
    }


    private void initViews() {
        statusText = (TextView) this.findViewById(R.id.status);
        locationTextView = (TextView) this.findViewById(R.id.currentLocationText);

        editText1 = (EditText) this.findViewById(R.id.jsonFirstParameter);
        editText2 = (EditText) this.findViewById(R.id.jsonSecondParameter);
        editText3 = (EditText) this.findViewById(R.id.jsonThirdParameter);
        sendJSONButton = (Button) this.findViewById(R.id.sendJSONButton);

        sendJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // postJsonToServer();
                try {

                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyy HH:mm");
                                        Date resultDate = new Date(location.getTime());

                                        long time = location.getTime();

                                        locationTextView.setText("Latitude: " + latitude + ", longitude: " + longitude + ", location time " + time + ", current time: " + simpleDateFormat.format(resultDate));
                                        logger.log("Latitude: " + latitude + ", longitude: " + longitude + ", location time " + time + ", current time: " + simpleDateFormat.format(resultDate));
                                    }
                                }
                            });
                } catch (SecurityException e) {
                    logger.log("Check location permissions", e);
                }


            }
        });
    }

    private void postJsonToServer() {
        if (validateSource() && isConnected) {
            //type url you want to post to
//            new PostJSONAsyncTask(this)
//                    .execute("https://requestb.in/11hw95m1", obtainData().toString());
            new PostJSONOkHttp(this).post("https://requestb.in/19xdfcv1", obtainData().toString());
        } else {
            Toast.makeText(this, "Fill out every line", Toast.LENGTH_SHORT).show();
        }

    }

    private JSONObject obtainData() {
        JSONObject postData = new JSONObject();

        try {

            //extract statics for future
            postData.accumulate("key1", editText1.getText());
            postData.accumulate("key2", editText2.getText());
            postData.accumulate("key3", editText3.getText());

        } catch (JSONException e) {
            logger.log("error", e);
        }
        return postData;
    }

    private boolean validateSource() {
        return !TextUtils.isEmpty(editText1.getText()) && !TextUtils.isEmpty(editText2.getText())
                && !TextUtils.isEmpty(editText3.getText());
    }
}
