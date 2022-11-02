package com.example.project;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.Scanner;

public class ItemDetailsAsync extends AsyncTask<String,Void,String> {
    private ItemDetailsActivity activity;
    public ItemDetailsAsync(ItemDetailsActivity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String...data){
        try {
            URL url = new URL(MyURL.url_base + MyURL.get_item);
            HttpURLConnection connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            Log.e("id2",data[0]);
            String parameters =
                    "token="+Session.Get(this.activity,"token")
                    +"&id="+data[0];
            Log.e("qs",parameters);
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
                    String title = jsonObject.getString("title");
                    String category = jsonObject.getString("category");
                    String price= jsonObject.getString("price");
                    String infos = jsonObject.getString("infos");
                    String image = jsonObject.getString("image");
                    String distance = jsonObject.getString("distance");
                    String pseudo = jsonObject.getString("pseudo");
                    String email = jsonObject.getString("email");
                    String pn = jsonObject.getString("pn");
                    String wl = jsonObject.getString("wl");
                    this.activity.Populate(title,category,price,infos,image,distance,pseudo,email,pn,wl);
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
