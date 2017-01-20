package com.example.hciproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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
        int skuId=this.getIntent().getIntExtra("skuId",0);
        String url="http://api.skroutz.gr/skus/"+skuId+"/products";
        new GetShops().execute(url);
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
                shop.setShopId(shopJson.getInt("shop_id"));
                shops.add(shop);
            }
        } catch (JSONException e) {
            return null;
        }
        return shops;
    }

}
