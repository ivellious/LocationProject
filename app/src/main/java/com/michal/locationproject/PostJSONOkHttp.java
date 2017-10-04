package com.michal.locationproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by michal on 04.10.17.
 */

public class PostJSONOkHttp {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Context context;
    private OkHttpClient httpClient;

    public PostJSONOkHttp(Context context) {
        this.context = context;
        httpClient = new OkHttpClient();
    }

    public void post(String url, String jsonData) {
        RequestBody requestBody = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context, "failiure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "bla" + response.code());

                try {

                    Intent i = new Intent("POST_ACTION");
                    i.putExtra("POST_RESPONSE", response.code());
                    context.sendBroadcast(i);
                } catch (Exception e) {
                    Log.e("TAG", "EXCEPTION", e);
                }

            }
        });

    }
}
