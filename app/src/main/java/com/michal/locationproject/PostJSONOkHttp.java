package com.michal.locationproject;

import android.content.Context;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by michal on 04.10.17.
 */

public class PostJSONOkHttp {

    public static final String POST_RESPONSE_EXTRA = "POST_RESPONSE";
    private static final String TAG = "PostJSONOkHttp";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Context context;
    private OkHttpClient httpClient;
    private SimpleLogger logger;
    private Callback mCallback;

    public PostJSONOkHttp(Context context, Callback callback) {
        this.context = context;
        this.mCallback = callback;
        httpClient = new OkHttpClient();
        logger = new SimpleLogger(TAG);
    }

    public void post(String url, String jsonData) {
        RequestBody requestBody = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        httpClient.newCall(request).enqueue(mCallback);

    }
}
