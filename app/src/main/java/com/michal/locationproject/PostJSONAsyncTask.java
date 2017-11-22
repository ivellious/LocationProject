package com.michal.locationproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * for comparision purposes
 * Created by michal on 04.10.17.
 */

public class PostJSONAsyncTask extends AsyncTask<String, Void, String> {

    Context mContext;

    public PostJSONAsyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        return post(params[0], params[1]);
    }

    private String post(String url, String data) {
        InputStream inputStream;
        String responseString = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            StringEntity se = new StringEntity(data);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                responseString = convertInputStreamToString(inputStream);
            else
                responseString = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return responseString;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(mContext, "Data was sent", Toast.LENGTH_SHORT).show();
        Log.e("TAG", result);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }
}
