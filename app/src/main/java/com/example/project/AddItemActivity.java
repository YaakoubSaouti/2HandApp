package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class AddItemActivity extends AppCompatActivity {
    public static final int SELECT_PICTURE = 1;
    public static final int TAKE_PICTURE = 2;
    public static final int CHOOSE_CATEGORY = 3;

    private String photo;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.wizard:
                    Intent intent = new Intent(AddItemActivity.this, CategoriesActivity.class);
                    startActivityForResult(intent, CHOOSE_CATEGORY);
                    break;
                case R.id.take_picture:
                    if (ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)==true)
                            ExplainNeedForCamera();
                        else AskPermissionForCamera();
                    }else TakePicture();
                    break;
                case R.id.select_picture:
                    if (ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)==true)
                            ExplainNeedForPictures();
                        else AskPermissionForPictures();
                    }else SelectPicture();
                    break;
                case R.id.confirm:
                    AddItem();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        if(savedInstanceState!=null){
            this.photo=savedInstanceState.getString("photo");
            if(this.photo!=null) {
                byte[] decodedBytes = Base64.decode(this.photo, Base64.URL_SAFE);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                ImageView picture = (ImageView) findViewById(R.id.picture);
                picture.setImageBitmap(decodedBitmap);
                picture.setVisibility(View.VISIBLE);
            }
        }
        Button select_picture = (Button) findViewById(R.id.select_picture);
        Button take_picture = (Button) findViewById(R.id.take_picture);
        Button confirm = (Button) findViewById(R.id.confirm);
        Button wizard = (Button) findViewById(R.id.wizard);
        select_picture.setOnClickListener(listener);
        take_picture.setOnClickListener(listener);
        confirm.setOnClickListener(listener);
        wizard.setOnClickListener(listener);
    }

    private void setPhoto(Bitmap photo){
        String encoded=null;
        if(photo != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        }
        this.photo = encoded;
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photo",this.photo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap photo = BitmapFactory.decodeStream(inputStream);
                ImageView picture = (ImageView) findViewById(R.id.picture);
                picture.setImageBitmap(photo);
                picture.setVisibility(View.VISIBLE);
                this.setPhoto(photo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView picture = (ImageView) findViewById(R.id.picture);
            picture.setImageBitmap(photo);
            picture.setVisibility(View.VISIBLE);
            this.setPhoto(photo);
        }
        if (requestCode == CHOOSE_CATEGORY){
            if(resultCode == RESULT_OK) {
                EditText category = (EditText) findViewById(R.id.category);
                category.setText(data.getStringExtra("category"));
            }
            if(resultCode == RESULT_CANCELED){
                if(data!=null && data.getIntExtra("code",0)==1){
                    Toast.makeText(AddItemActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG);
                }
            }
        }
    }
    private void AddItem() {
        EditText et1=(EditText) findViewById(R.id.name);
        EditText et2=(EditText) findViewById(R.id.category);
        EditText et3=(EditText) findViewById(R.id.price);
        EditText et4=(EditText) findViewById(R.id.desc);
        String name = et1.getText().toString();
        String category = et2.getText().toString();
        String price = et3.getText().toString();
        String desc = et4.getText().toString();
        new AddItemAsync(AddItemActivity.this).execute(name,category,price,desc,photo);
    }

    private void SelectPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_PICTURE);
    }

    private void TakePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PICTURE);
    }

    public void IndicateSuccess() {
        Intent intent=new Intent(AddItemActivity.this,HomeActivity.class);
        intent.putExtra("code",1);
        startActivity(intent);
        finish();
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
                Intent intent = new Intent(AddItemActivity.this,MainActivity.class);
                intent.putExtra("code",2);
                startActivity(intent);
                finish();
                break;
            case 7:
                error_message = getResources().getString(R.string.error_category);
                break;
            case 8:
                error_message = getResources().getString(R.string.name_error);
                break;
            case 9:
                error_message = getResources().getString(R.string.price_error);
                break;
            case 10:
                error_message = getResources().getString(R.string.desc_error);
                break;
        }
        Toast.makeText(AddItemActivity.this, error_message, Toast.LENGTH_LONG).show();
    }

    private void ExplainNeedForCamera()
    {
        Snackbar.make(findViewById(R.id.layout),
                getResources().getString(R.string.need_camera),
                Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.turn_on), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        AskPermissionForCamera();
                    }
                }).show();
    }

    private void AskPermissionForCamera()
    {
        ActivityCompat.requestPermissions
                (this, new String[]{Manifest.permission.CAMERA }, 6);
    }

    private void ExplainNeedForPictures()
    {
        Snackbar.make(findViewById(R.id.layout),
                getResources().getString(R.string.need_pictures),
                Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.turn_on), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        AskPermissionForPictures();
                    }
                }).show();
    }

    private void AskPermissionForPictures()
    {
        ActivityCompat.requestPermissions
                (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE }, 7);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantedResults)
    {
        if(code == 6)
        {
            if(grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                TakePicture();
            }
            else if(shouldShowRequestPermissionRationale(permissions[0]) == false) {
                DisplaySettings();
            }
            else{
                ExplainNeedForCamera();
            }
        }
        if(code == 7)
        {
            if(grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectPicture();
            }
            else if(shouldShowRequestPermissionRationale(permissions[0]) == false) {
                DisplaySettings();
            }
            else{
                ExplainNeedForPictures();
            }
        }
        super.onRequestPermissionsResult(code, permissions, grantedResults);
    }
    private void DisplaySettings()
    {
        Snackbar.make(findViewById(R.id.layout),
                getResources().getString(R.string.turned_off_permission), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.settings), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        final Intent intent =
                                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        final Uri uri = Uri.fromParts("package", "test_gps", null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();
    }
}