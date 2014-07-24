package com.cloudmediaplus.followme.framework;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PostMan {

    public void send(String phoneNumber, Location location) {
        if(!phoneNumber.isEmpty()){
            String url = "http://www.almogtarbeen.com/follow/update/" + phoneNumber + "/" + location.getLatitude() + "/" + location.getLongitude();
            Log.i("C2DM", url);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {
            });
        }
    }

    public void post(final String url, final List<NameValuePair> nameValuePairs){

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                try {
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(post);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        Log.e("HttpResponse", line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }
}