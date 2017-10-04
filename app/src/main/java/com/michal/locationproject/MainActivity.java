package com.michal.locationproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText editText1, editText2, editText3;
    private TextView statusText;
    private Button sendJsonButtton;
    private boolean isConnected;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                log("intent is null");
                return;
            }

            if (intent.getExtras().isEmpty()) {
                log("extras are empty");
                return;
            }

            showToast("Response Code + " + intent.getExtras().getInt("POST_RESPONSE"));
        }
    };

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        this.registerReceiver(mBroadcastReceiver, new IntentFilter("POST_ACTION"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
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

        editText1 = (EditText) this.findViewById(R.id.jsonFirstParameter);
        editText2 = (EditText) this.findViewById(R.id.jsonSecondParameter);
        editText3 = (EditText) this.findViewById(R.id.jsonThirdParameter);
        sendJsonButtton = (Button) this.findViewById(R.id.sendJSONButton);

        sendJsonButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJsonToServer();
            }
        });
    }

    private void postJsonToServer() {
        if (validateSource() && isConnected) {
            //type url you want to post to
//            new PostJSONAsyncTask(this)
//                    .execute("https://requestb.in/11hw95m1", obtainData().toString());
new PostJSONOkHttp(this).post("https://requestb.in/1dhsheu1", obtainData().toString());
        } else {
            Toast.makeText(this, "empty values!", Toast.LENGTH_SHORT).show();
        }

    }

    private JSONObject obtainData() {
        JSONObject postData = new JSONObject();

        try {
            postData.accumulate("key1", editText1.getText());
            postData.accumulate("key2", editText2.getText());
            postData.accumulate("key3", editText3.getText());

        } catch (JSONException e) {
            Log.e("TAG", "error", e);
        }
        return postData;
    }

    private boolean validateSource() {
        return !TextUtils.isEmpty(editText1.getText()) && !TextUtils.isEmpty(editText2.getText())
                && !TextUtils.isEmpty(editText3.getText());
    }


    private void log(String message) {
        Log.e(TAG, message);
    }
}
