package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailsActivity extends AppCompatActivity {
    private String id;
    private int come_from;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.add_wishlist){
                new ManageWishListAsync(ItemDetailsActivity.this).execute(id);
            }
            if(view.getId()==R.id.remove_wishlist){
                new ManageWishListAsync(ItemDetailsActivity.this).execute(id);
            }
            if(view.getId()==R.id.delete_item){
                new DeleteItemAsync(ItemDetailsActivity.this).execute(id);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Button add_wishlist = (Button) findViewById(R.id.add_wishlist);
        Button remove_wishlist = (Button) findViewById(R.id.remove_wishlist);
        Button delete_item = (Button) findViewById(R.id.delete_item);
        add_wishlist.setOnClickListener(listener);
        remove_wishlist.setOnClickListener(listener);
        delete_item.setOnClickListener(listener);
        Intent catch_intent = getIntent();
        if(catch_intent.hasExtra("id") && catch_intent.hasExtra("come_from")){
            String id = String.valueOf(catch_intent.getIntExtra("id",-1));
            int come_from = catch_intent.getIntExtra("come_from",-1);
            if(come_from==1){
                if(Integer.parseInt(id)>-1){
                    this.id=id;
                    this.come_from=come_from;
                    new ItemDetailsAsync(ItemDetailsActivity.this).execute(this.id);
                }else{
                    finish();
                }
            }else if(come_from==2){
                if(Integer.parseInt(id)>-1){
                    this.id=id;
                    this.come_from=come_from;
                    new ItemDetailsAsync(ItemDetailsActivity.this).execute(this.id);
                }else{
                    finish();
                }
            }else{
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("id",id);
    }

    public void Populate(String title, String cat, String pri, String inf, String img, String dist, String pseud, String em, String pn, String wl) {
        Button add_wishlist = (Button) findViewById(R.id.add_wishlist);
        Button remove_wishlist = (Button) findViewById(R.id.remove_wishlist);
        Button delete_item = (Button) findViewById(R.id.delete_item);
        if(!img.equals("0")){
            ImageView picture = (ImageView) findViewById(R.id.picture);
            byte[] decodedBytes = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            picture.setImageBitmap(decodedBitmap);
            picture.setVisibility(View.VISIBLE);
        }
        TextView name=(TextView) findViewById(R.id.name);
        name.setText(title);
        TextView category=(TextView) findViewById(R.id.category);
        category.setText(cat);
        TextView price=(TextView) findViewById(R.id.price);
        price.setText(pri+" $");
        TextView infos=(TextView) findViewById(R.id.infos);
        infos.setText(inf);
        if(this.come_from==1){
            TextView distance=(TextView) findViewById(R.id.distance);
            distance.setText("+/- "+dist+" km");
            TextView pseudo=(TextView) findViewById(R.id.pseudo);
            pseudo.setText(pseud);
            TextView contact=(TextView) findViewById(R.id.contact);
            String contactString = "";
            if(!em.isEmpty()){
                contactString+=getResources().getString(R.string.email)+em;
            }
            if(!pn.isEmpty()){
                contactString+="\n"+getResources().getString(R.string.phone_number)+pn;
            }
            contact.setText(contactString);
            LinearLayout not_my_item_details = (LinearLayout) findViewById(R.id.not_my_item_details);
            not_my_item_details.setVisibility(View.VISIBLE);
            if(Boolean.valueOf(wl)){
                add_wishlist.setVisibility(View.GONE);
                remove_wishlist.setVisibility(View.VISIBLE);
            }else{
                remove_wishlist.setVisibility(View.GONE);
                add_wishlist.setVisibility(View.VISIBLE);
            }
        }else{
            delete_item.setVisibility(View.VISIBLE);
        }
    }

    public void IndicateError(String error){
        String error_message = null;
        switch (Integer.parseInt(error)) {
            case 1: case 2: case 3: case 4 : case 5:
                error_message = getResources().getString(R.string.network_error);
                break;
            case 6:
                Intent intent = new Intent(ItemDetailsActivity.this,MainActivity.class);
                intent.putExtra("code",2);
                startActivity(intent);
                finish();
                break;
            case 7:
                finish();
                break;
        }
        Toast.makeText(ItemDetailsActivity.this,error_message,Toast.LENGTH_LONG).show();
    }

    public void SuccessDelete() {
        finish();
    }

    public void SuccessWishList() {
        new ItemDetailsAsync(ItemDetailsActivity.this).execute(this.id);
    }
}