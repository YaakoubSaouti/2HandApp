package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class WishlistActivity extends AppCompatActivity {

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(WishlistActivity.this,ItemDetailsActivity.class);
            intent.putExtra("id",view.getId());
            intent.putExtra("come_from",1);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        new WishlistAsync(WishlistActivity.this).execute();
    }

    public void IndicateError(String error){
        String error_message = null;
        switch (Integer.parseInt(error)) {
            case 1: case 2: case 3: case 4 : case 5:
                error_message = getResources().getString(R.string.network_error);
                break;
            case 6:
                Intent intent = new Intent(WishlistActivity.this,MainActivity.class);
                intent.putExtra("code",2);
                startActivity(intent);
                finish();
                break;
            case 7:
                error_message = getResources().getString(R.string.error_no_item3);
                break;
        }
        LinearLayout items = findViewById(R.id.items);
        items.removeAllViews();
        Toast.makeText(WishlistActivity.this,error_message,Toast.LENGTH_LONG).show();
    }

    public void Populate(JSONArray ids, JSONArray titles, JSONArray prices, JSONArray images) {
        LinearLayout items = findViewById(R.id.items);
        items.removeAllViews();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        int dim2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim2, dim2);
        params.setMargins(dim, dim, dim, dim);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dim, dim, dim, dim);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dim, dim, dim, dim);
        for(int i = 0; i < ids.length(); i++){
            Log.e("i",String.valueOf(i));
            LinearLayout ly=new LinearLayout(WishlistActivity.this);
            ly.setOrientation(LinearLayout.HORIZONTAL);
            ImageView image = new ImageView(WishlistActivity.this);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            TextView text = new TextView(WishlistActivity.this);
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
                Log.e("idasfasdf",ids.getString(i));
                ly.setId(Integer.parseInt(ids.getString(i)));
                ly.setOnClickListener(listener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.addView(ly,params3);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        new WishlistAsync(WishlistActivity.this).execute();
    }
}