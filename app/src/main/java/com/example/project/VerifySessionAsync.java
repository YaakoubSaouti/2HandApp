package com.example.project;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class VerifySessionAsync extends AsyncTask<String,Void,String> {
    private MainActivity activity;
    public VerifySessionAsync (MainActivity activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String ... data) {
        if(data[0]!=null) {
            try {
                URL url = new URL(MyURL.url_base + MyURL.verify_token + "?token=" + data[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    Scanner scanner = new Scanner(inputStreamReader);
                    scanner.useDelimiter("\n");
                    String response = "";
                    while (scanner.hasNext()) {
                        response += scanner.next();
                    }
                    return response;
                } else {
                    return "2";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "1";
            } catch (IOException e) {
                e.printStackTrace();
                return "3";
            }
        }
        return "4";
    }

    protected void onPostExecute(String jsonString){
        Log.e("json",jsonString);
        if(jsonString.equals("1") || jsonString.equals("2") || jsonString.equals("3") || jsonString.equals("4")){
            this.activity.checkSession(false);
        }else{
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                boolean response = jsonObject.getBoolean("response");
                this.activity.checkSession(response);
            } catch (JSONException e) {
                e.printStackTrace();
                this.activity.checkSession(false);
            }
        }
    }
}

