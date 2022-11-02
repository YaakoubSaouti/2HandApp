package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.sign_up_btn) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
            if (view.getId() == R.id.log_in_btn) {
                EditText username_login = (EditText) findViewById(R.id.username_login);
                EditText password_login = (EditText) findViewById(R.id.password_login);
                String un = username_login.getText().toString();
                String pw = password_login.getText().toString();
                new MainAsync(MainActivity.this).execute(un, pw);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new VerifySessionAsync(MainActivity.this).execute(Session.Get(MainActivity.this,"token"));
        Intent intent = getIntent();
        if (intent.getIntExtra("code", 1) == 0) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.success_registration), Toast.LENGTH_LONG).show();
        }
        if (intent.getIntExtra("code", 1) == 2) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.error_session), Toast.LENGTH_LONG).show();
        }
        Button sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(listener);
        Button log_in_btn = findViewById(R.id.log_in_btn);
        log_in_btn.setOnClickListener(listener);
    }

    public void IndicateError(String error) {
        String error_message = null;
        switch (Integer.parseInt(error)) {
            case 1:
            case 2:
            case 3:
            case 4:
                error_message = getResources().getString(R.string.network_error);
                break;
            case 5:
                error_message = getResources().getString(R.string.empty_fields_error);
                break;
            case 6:
                error_message = getResources().getString(R.string.not_valid_credentials);
                break;
        }
        Toast.makeText(MainActivity.this, error_message, Toast.LENGTH_LONG).show();
    }

    public void CreateSession(String token) {
        Session.Set(MainActivity.this,"token",token);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void checkSession(boolean response) {
        if(response){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}