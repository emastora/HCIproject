package com.example.hciproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Schoox on 1/19/2017.
 */

public class SearchActivity extends AppCompatActivity {

    private SearchActivity thisActivity;
    private ArrayList<Category> categories;

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        thisActivity=this;
        final EditText search= (EditText) this.findViewById(R.id.search);
        Button ok= (Button) this.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="http://api.skroutz.gr/search?q=";
                String searchPhrase=search.getText().toString();
                if(searchPhrase.length()<3){
                    Toast.makeText(thisActivity, "has errors", Toast.LENGTH_LONG).show();
                    return;
                }
                url=url+searchPhrase;

                new DoSearch().execute(url);
            }
        });

        listView= (ListView) this.findViewById(R.id.lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(thisActivity,ProductListActivity.class);
                intent.putExtra("categoryId",categories.get(position).getId());
                thisActivity.startActivity(intent);
            }
        });
    }

    class DoSearch extends AsyncTask<String,String,String>{


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
                    categories=parse(s);
                    listView.setAdapter(new AdapterCategory(thisActivity,0,categories));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private ArrayList<Category> parse(String response){
        ArrayList<Category> categoriesResult=new ArrayList<>();
        try {
            JSONArray categories=new JSONObject(response).getJSONArray("categories");
            for(int i=0;i<categories.length();i++){
                Category newCategory=new Category();
                newCategory.setId(categories.getJSONObject(i).getInt("id"));
                newCategory.setImageUrl(categories.getJSONObject(i).getString("image_url"));
                newCategory.setName(categories.getJSONObject(i).getString("name"));
                categoriesResult.add(newCategory);
            }
        } catch (JSONException e) {
            return null;
        }
        return categoriesResult;
    }
}
