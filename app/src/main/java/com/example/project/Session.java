package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class Session {
    private final static String path = "com.example.project";
    public static void Set(Context ctx,String key,String value) {
        SharedPreferences prefs = ctx.getSharedPreferences(path,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token",value).apply();
    }

    public static String Get(Context ctx,String key) {
        SharedPreferences prefs = ctx.getSharedPreferences(path,Context.MODE_PRIVATE);
        String value = prefs.getString(key,null);
        return value;
    }
    public static void Destroy(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(path,Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
