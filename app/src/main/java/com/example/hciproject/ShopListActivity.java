package com.example.hciproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Schoox on 1/20/2017.
 */

public class ShopListActivity extends AppCompatActivity {

    private ShopListActivity thisActivity;
    private ListView listview;
    private ArrayList<Shop> shops;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_card);
        thisActivity=this;
        listview=(ListView)findViewById(R.id.lv);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent camera=new Intent(thisActivity,CameraActivity.class);
                camera.putExtra("id",shops.get(position).getShopId());
                thisActivity.startActivity(camera);
            }
        });
        int skuId=this.getIntent().getIntExtra("skuId",0);

        String image=this.getIntent().getStringExtra("image");
        String name=this.getIntent().getStringExtra("name");
        int shops=this.getIntent().getIntExtra("shops",0);

        ImageView imageView=(ImageView)this.findViewById(R.id.category_image);
        Picasso.with(thisActivity).load(image).into(imageView);
        TextView title=(TextView)this.findViewById(R.id.name);
        title.setText(name);
        TextView numStores=(TextView)this.findViewById(R.id.num_stores);
        numStores.setText("Stores:"+shops);
        TextView price=(TextView)this.findViewById(R.id.price);
        price.setVisibility(View.GONE);


        String url="http://api.skroutz.gr/skus/"+skuId+"/products";
        new GetShops().execute(url);
    }

    private void initializeFirstCell(String image,String name,int shops){

    }

    class GetShops extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            if(new Network(thisActivity).isTokenExpired(thisActivity)){
                Intent intent=new Intent(thisActivity,MainActivity.class);
                thisActivity.startActivity(intent);
                thisActivity.finish();
            }
            String response=new Network(thisActivity).doGetRequest(thisActivity,params[0],true);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("debug","response:"+s);

            try {
                if(new JSONObject(s).has("errors")) {
                    Toast.makeText(thisActivity, "has errors", Toast.LENGTH_LONG).show();
                }else{
                    shops=parse(s);
                    listview.setAdapter(new AdapterShops(thisActivity,0,shops));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private ArrayList<Shop> parse(String response){
        ArrayList<Shop> shops=new ArrayList<>();
        try {
            JSONArray categories=new JSONObject(response).getJSONArray("products");
            for(int i=0;i<categories.length();i++){
                Shop shop=new Shop();
                JSONObject shopJson=categories.getJSONObject(i);
                shop.setName(shopJson.getString("name"));
                shop.setPrice(shopJson.getDouble("price"));
                shop.setImmediate(shopJson.getBoolean("immediate_pickup"));
                shop.setAvailabilityString(shopJson.getString("availability"));
                shop.setShopId(shopJson.getInt("shop_id"));
                shops.add(shop);
            }
        } catch (JSONException e) {
            return null;
        }
        return shops;
    }

}
