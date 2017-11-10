package com.michal.locationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by michal on 18.10.17.
 */

public class FirstLogInActivity extends AppCompatActivity {


    private EditText nameEdiText, surnameEditText;
    private Button submitButton;
    private SimpleLogger mLogger;

    @Override
    protected void onResume() {
        super.onResume();
        if (isUserEnrolled()) {
            Intent intent = new Intent(FirstLogInActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        mLogger = new SimpleLogger(FirstLogInActivity.class.getSimpleName());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Location App"," Something went wrong", paramThrowable);
            }
        });




        nameEdiText =(EditText) findViewById(R.id.insertNameEditText);
        surnameEditText = (EditText) findViewById(R.id.insertSurnameEditText);
        submitButton = (Button) findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameEdiText.getText()) || TextUtils.isEmpty(surnameEditText.getText())) {
                    Toast.makeText(getApplicationContext(), "Fill name an surname", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    registerUser();
                } catch (JSONException e) {
                    mLogger.log("JSON FAIL", e);
                }
            }
        });
    }

    public boolean isUserEnrolled() {
        return false;
    }


    private void registerUser() throws JSONException {
        new PostJSONOkHttp(this, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
              //  Toast.makeText(getApplicationContext(), "Registration Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    LocationAppSharedPreferences sharedPreferences = new LocationAppSharedPreferences(getApplicationContext());
                    sharedPreferences.saveNameToSharedPreferences(nameEdiText.getText().toString());
                    sharedPreferences.saveSurnameToSharedPreferences(surnameEditText.getText().toString());
                    //// TODO: 10.11.17 add token from response to shared prefs
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FirstLogInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

                } else {
                    showToast("elooo");
                }



            }
        }).post("https://requestb.in/19xdfcv1", getRegistrationData());
    }


    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(FirstLogInActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRegistrationData() throws JSONException {
        JSONObject postData = new JSONObject();

        postData.accumulate(LocationAppSharedPreferences.USER_NAME_KEY, nameEdiText.getText());
        postData.accumulate(LocationAppSharedPreferences.USER_SURNAME_KEY, surnameEditText.getText());
        return postData.toString();
    }


}
