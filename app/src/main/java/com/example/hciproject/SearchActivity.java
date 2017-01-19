package com.example.hciproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Schoox on 1/19/2017.
 */

public class SearchActivity extends AppCompatActivity {

    private SearchActivity thisActivity;

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
                String url="";
                String searchPhrase=search.getText().toString();
                new DoSearch().execute(url);
            }
        });
    }

    class DoSearch extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            new Network(thisActivity).doGetRequest(thisActivity,params[0],true);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
