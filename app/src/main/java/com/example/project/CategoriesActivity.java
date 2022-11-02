package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class CategoriesActivity extends AppCompatActivity {

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent result_intent = new Intent();
            TextView tv = (TextView) view;
            result_intent.putExtra("category",tv.getText().toString());
            setResult(RESULT_OK, result_intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        new CategoriesAsync(CategoriesActivity.this).execute();
    }

    public void IndicateError() {
        Intent result_intent = new Intent();
        result_intent.putExtra("code",1);
        setResult(RESULT_CANCELED, result_intent);
        finish();
    }

    public void Populate(JSONArray categories) {
        for(int i=0; i<categories.length();i++){
            try {
                String category = categories.getString(i);
                TextView textView = new TextView(new ContextThemeWrapper(this, R.style.one_line_displaying), null, 0);
                textView.setText(category);
                textView.setOnClickListener(listener);
                LinearLayout toPopulate = (LinearLayout)findViewById(R.id.toPopulate);
                toPopulate.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}