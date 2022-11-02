package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {
    private View baseView;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        }
        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.sign_up_btn){
                String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                if (ActivityCompat.checkSelfPermission(SignUpActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
                    if (shouldShowRequestPermissionRationale(permission)) ExplainNeed();
                    else AskPermission();
                }else{ SendFormInfos();}
            }
            if(view.getId()==R.id.log_in_btn){
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                intent.putExtra("code",1);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.baseView = findViewById(R.id.layout);
        Button sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(listener);
        Button log_in_btn = findViewById(R.id.log_in_btn);
        log_in_btn.setOnClickListener(listener);
    }

    public void IndicateError(String error){
        String error_message = null;
        switch (Integer.parseInt(error)) {
            case 1: case 2: case 3: case 4:
                error_message = getResources().getString(R.string.network_error);
                break;
            case 5:
                error_message = getResources().getString(R.string.empty_fields_error);
                break;
            case 6:
                error_message = getResources().getString(R.string.short_username_error);
                break;
            case 7:
                error_message = getResources().getString(R.string.same_username_error);
                break;
            case 8:
                error_message = getResources().getString(R.string.short_password_error);
                break;
            case 9:
                error_message = getResources().getString(R.string.different_password_error);
                break;
            case 10:
                error_message = getResources().getString(R.string.location_error);
                break;
            case 11:
                error_message = getResources().getString(R.string.email_error);
                break;
            case 12:
                error_message = getResources().getString(R.string.phone_number_error);
                break;
        }
        Toast.makeText(SignUpActivity.this,error_message,Toast.LENGTH_LONG).show();
    }

    public void IndicateSuccess(){
        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        intent.putExtra("code",0);
        startActivity(intent);
    }

    private void ExplainNeed()
    {
        Snackbar.make(baseView,
                getResources().getString(R.string.explain_need),
                Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.turn_on), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        AskPermission();
                    }
                }).show();
    }

    private void AskPermission()
    {
        ActivityCompat.requestPermissions
                (this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, 6);
    }
    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantedResult)
    {
        if(code == 6){
            if(grantedResult[0] == PackageManager.PERMISSION_GRANTED){
                SendFormInfos();
            }else if(!shouldShowRequestPermissionRationale(permissions[0])){
                DisplaySettings();
            }else{
                ExplainNeed();
            }
        }
        super.onRequestPermissionsResult(code, permissions, grantedResult);
    }

    private void DisplaySettings()
    {
        Snackbar.make(baseView,
                getResources().getString(R.string.turned_off_permission),
                Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        final Uri uri = Uri.fromParts("package", "test_gps", null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();
    }

    private void SendFormInfos(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Snackbar.make(baseView,
                    getResources().getString(R.string.turn_on_gps),
                    Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }else {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        locationListener
                );
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.gps_error), Toast.LENGTH_SHORT).show();
                }
                if (loc != null) {
                    double a = loc.getLatitude();
                    double b = loc.getLongitude();
                    EditText username_registration = (EditText) findViewById(R.id.username_registration);
                    EditText password_registration1 = (EditText) findViewById(R.id.password_registration1);
                    EditText password_registration2 = (EditText) findViewById(R.id.password_registration2);
                    EditText email_registration = (EditText) findViewById(R.id.email_registration);
                    EditText phone_number_registration = (EditText) findViewById(R.id.phone_number_registration);
                    a = ((int)(a * 100000)) / 100000.00000;
                    b = ((int)(b * 100000)) / 100000.00000;
                    String lat= String.valueOf(a);
                    String lon = String.valueOf(b);
                    String un = username_registration.getText().toString();
                    String pw1 = password_registration1.getText().toString();
                    String pw2 = password_registration2.getText().toString();
                    String email = email_registration.getText().toString();
                    String pn = phone_number_registration.getText().toString();
                    new SignUpAsync(SignUpActivity.this).execute(un,pw1,pw2,lon,lat,email,pn);
                }
            }
        }

    }
}