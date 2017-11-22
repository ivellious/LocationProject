package com.michal.locationproject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
/**
 * Created by michal on 04.10.17.
 */

class PostJSONOkHttp {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpClient;
    private Callback mCallback;

    PostJSONOkHttp(Callback callback) {
        this.mCallback = callback;
        httpClient = new OkHttpClient();
    }

    void post(String url, String jsonData) {
        RequestBody requestBody = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        httpClient.newCall(request).enqueue(mCallback);
    }
}
