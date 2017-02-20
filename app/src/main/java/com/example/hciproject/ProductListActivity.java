package com.example.hciproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Schoox on 1/20/2017.
 */

public class ProductListActivity extends AppCompatActivity {

    private ListView list;
    private int categoryId;
    private Activity thisActivity;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        thisActivity=this;
        list=(ListView)findViewById(R.id.product_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(thisActivity,ShopListActivity.class);
                intent.putExtra("skuId",products.get(position).getId());
                intent.putExtra("name",products.get(position).getName());
                intent.putExtra("image",products.get(position).getImageUrl());
                intent.putExtra("shops",products.get(position).getNumberStores());
                thisActivity.startActivity(intent);
            }
        });

        categoryId=this.getIntent().getIntExtra("categoryId",0);
        String url="http://api.skroutz.gr/categories/"+categoryId+"/skus";

        new GetProducts().execute(url);
    }

    class GetProducts extends AsyncTask<String,String,String>{

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
            products=parse(s);
            if(products==null) return;
            list.setAdapter(new AdapterProduct(thisActivity,0,products));

        }
    }

    ArrayList<Product> parse(String response){
        ArrayList<Product> result=new ArrayList<>();

        try {
            JSONArray skus=new JSONObject(response).getJSONArray("skus");
            for(int i=0;i<skus.length();i++){
                Product newProduct=new Product();
                JSONObject productJson=skus.getJSONObject(i);
                newProduct.setId(productJson.getInt("id"));
                newProduct.setImageUrl(productJson.getJSONObject("images").getString("main"));
                newProduct.setMinPrice(productJson.getDouble("price_min"));
                newProduct.setName(productJson.getString("display_name"));
                newProduct.setNumberStores(productJson.getInt("shop_count"));
                result.add(newProduct);
            }
        } catch (JSONException e) {
            return null;
        }

        return result;
    }
}
