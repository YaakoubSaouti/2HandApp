package com.example.project;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class AddItemAsync extends AsyncTask<String,Void,String> {
    AddItemActivity activity;
    public AddItemAsync(AddItemActivity activity){
        this.activity=activity;
    }

    @Override
    protected String doInBackground(String...data){
        try {
            URL url = new URL(MyURL.url_base + MyURL.add_item);
            HttpURLConnection connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String parameters =
                    "token="+Session.Get(this.activity,"token")
                    +"&name="+data[0]
                    +"&category="+data[1]
                    +"&price="+data[2]
                    +"&desc="+data[3];
            if(data[3]!=null){
                parameters+="&img="+ data[4];
            }
            bufferedWriter.write(parameters);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode==200){
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                Scanner scanner = new Scanner(inputStreamReader);
                scanner.useDelimiter("\n");
                String response = "";
                while(scanner.hasNext()){
                    response+=scanner.next();
                }
                return response;
            }else{
                return "3";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "1";
        } catch (IOException e) {
            e.printStackTrace();
            return "2";
        }
    }

    protected void onPostExecute(String jsonString){
        Log.e("jsonString:",jsonString);
        if(jsonString.equals("1") || jsonString.equals("2") || jsonString.equals("3")){
            this.activity.IndicateError(jsonString);
        }else{
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if(jsonObject.isNull("error_code")){
                    this.activity.IndicateSuccess();
                }else{
                    int error_code = jsonObject.getInt("error_code");
                    this.activity.IndicateError(String.valueOf(error_code));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.activity.IndicateError("4");
            }
        }
    }
}
