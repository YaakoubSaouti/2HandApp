package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

public class HomeActivity extends AppCompatActivity {
    private String km;
    private View baseView;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("km",this.km);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch(view.getId()){
                case R.id.profile:
                    intent = new Intent(HomeActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.add_item:
                    intent = new Intent(HomeActivity.this,AddItemActivity.class);
                    startActivity(intent);
                    break;
                case R.id.wishlist:
                    intent = new Intent(HomeActivity.this,WishlistActivity.class);
                    startActivity(intent);
                    break;
                case R.id.disconnect:
                    Session.Destroy(HomeActivity.this);
                    intent = new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.ok:
                    EditText distance = (EditText)findViewById(R.id.distance);
                    km = distance.getText().toString();
                    String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
                        if (shouldShowRequestPermissionRationale(permission)) ExplainNeed();
                        else AskPermission();
                    }else{ SearchByDistance();}
                    break;
                default :
                    intent = new Intent(HomeActivity.this,ItemDetailsActivity.class);
                    intent.putExtra("id",view.getId());
                    intent.putExtra("come_from",1);
                    startActivity(intent);
                    break;
            }
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        if(intent.getIntExtra("code", 0) == 1) {
            Toast.makeText(HomeActivity.this, getResources().getString(R.string.success_sale), Toast.LENGTH_LONG).show();
        }
        if(savedInstanceState==null){
            this.km="25";
        }else{
            this.km=savedInstanceState.getString("km");
        }
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(permission)) ExplainNeed();
            else AskPermission();
        }else{ SearchByDistance();}
        baseView = findViewById(R.id.baseView);
        TextView profile = (TextView) findViewById(R.id.profile);
        TextView add_item = (TextView) findViewById(R.id.add_item);
        TextView wishlist = (TextView) findViewById(R.id.wishlist);
        TextView disconnect = (TextView) findViewById(R.id.disconnect);
        Button ok = (Button) findViewById(R.id.ok);

        profile.setOnClickListener(listener);
        add_item.setOnClickListener(listener);
        wishlist.setOnClickListener(listener);
        disconnect.setOnClickListener(listener);
        ok.setOnClickListener(listener);
    }

    public void IndicateError(String error){
        String error_message = null;
        switch (Integer.parseInt(error)) {
            case 1: case 2: case 3: case 4 : case 5:
                error_message = getResources().getString(R.string.network_error2);
                break;
            case 6:
                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                intent.putExtra("code",2);
                startActivity(intent);
                finish();
                break;
            case 7:
                error_message = getResources().getString(R.string.location_error);
                break;
            case 8:
                error_message = getResources().getString(R.string.distance_format_error);
                break;
            case 9:
                error_message = getResources().getString(R.string.error_no_item);
                break;
        }
        Toast.makeText(HomeActivity.this,error_message,Toast.LENGTH_LONG).show();
        LinearLayout items = (LinearLayout) findViewById(R.id.items);
        items.removeAllViews();
    }

    public void Populate(JSONArray ids,JSONArray titles,JSONArray prices,JSONArray images) {
        TextView display_distance = (TextView) findViewById(R.id.display_distance);
        display_distance.setText("<"+this.km+" km");
        LinearLayout items = findViewById(R.id.items);
        items.removeAllViews();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int dim2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        int dim3 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim2, dim2);
        params.setMargins(dim, dim, dim, dim);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dim, dim, dim, dim);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dim, dim, dim, dim);
        int i;
        for(i = 0; i < ids.length(); i++){
            LinearLayout ly=new LinearLayout(HomeActivity.this);
            ly.setOrientation(LinearLayout.HORIZONTAL);
            ImageView image = new ImageView(HomeActivity.this);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            TextView text = new TextView(HomeActivity.this);
            text.setTextColor(getResources().getColor(R.color.white));
            try {
                if(!images.getString(i).equals("0")){
                    byte[] decodedBytes = Base64.decode(images.getString(i), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    image.setImageBitmap(decodedBitmap);
                }
                text.setText(
                         titles.getString(i) + "\n"
                        +prices.getString(i)+ " $\n");
                ly.addView(image,params);
                ly.addView(text,params2);
                if(i%2==0) ly.setBackgroundColor(getResources().getColor(R.color.purple_200));
                else ly.setBackgroundColor(getResources().getColor(R.color.purple_500));
                ly.setId(Integer.parseInt(ids.getString(i)));
                ly.setOnClickListener(listener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.addView(ly,params3);
        }
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
                SearchByDistance();
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

    private void SearchByDistance(){
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
                    TextView tv = (TextView) findViewById(R.id.display_distance);
                    tv.setText("< " + this.km + " km");
                    double a = loc.getLatitude();
                    double b = loc.getLongitude();
                    a = ((int)(a * 100000)) / 100000.00000;
                    b = ((int)(b * 100000)) / 100000.00000;
                    String lat = String.valueOf(a);
                    String lon = String.valueOf(b);
                    new HomeAsync(HomeActivity.this).execute(lon,lat,this.km);
                }
            }
        }
    }
}