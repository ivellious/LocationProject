package com.michal.locationproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        mLogger = new SimpleLogger(FirstLogInActivity.class.getSimpleName());


        nameEdiText = (EditText) findViewById(R.id.insertNameEditText);
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

        return !new LocationAppSharedPreferences(this).getNameFromSharedPreferences().equals("");
    }


    private void registerUser() throws JSONException {
        new PostJSONOkHttp(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    LocationAppSharedPreferences sharedPreferences = new LocationAppSharedPreferences(getApplicationContext());
                    String responseString = "";
                    if (response.body() != null) {
                        responseString = response.body().string();
                    }
                    mLogger.log("response string : " + responseString);
                    JSONObject responseJson;
                    if (TextUtils.isEmpty(responseString)) {
                        showToast("response string is null");
                        return;
                    }
                    try {
                        responseJson = new JSONObject(responseString);
                        sharedPreferences.saveToken(Integer.valueOf(responseJson.get("id").toString()));

                    } catch (JSONException e) {
                        mLogger.log("converting response", e);
                    }
                    mLogger.log("id: " + sharedPreferences.getToken());
                    sharedPreferences.saveNameToSharedPreferences(nameEdiText.getText().toString());
                    sharedPreferences.saveSurnameToSharedPreferences(surnameEditText.getText().toString());
                    showToast("Registration successful");
                    Intent intent = new Intent(FirstLogInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    showToast("Response is not 200");
                }


            }
        }).post(NetworkUtils.URL + "/person", getRegistrationData());
    }


    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
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
