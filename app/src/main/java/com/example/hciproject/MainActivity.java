package com.example.hciproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button skroutzLoginButton;
    private MainActivity thisActivity;
    String secret="K3j/0biTBg1QLkXGMrs68COfWsQaBewIVcec3d0BTMEwsCzQLAWdzpsjrtg4e4hpUDunnHLjlpBLDMM1QEsw==";
    String redirectionUrl="www.manos.mastorakis.gr";
    String responceType="code";
    String scope="public";
    String clientId="vZrtR94kaCqLtlzGrRwGFg==";
    String grantType="client_credentials";
    private String params;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity=this;
        preferences=getSharedPreferences("credentials",MODE_PRIVATE);
        Map<String,?> keys = preferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }

        preferences=getSharedPreferences("credentials",MODE_PRIVATE);
        if(!preferences.getString("token","default").equals("default")){
            //EXOUME ENA UPARXON TOKEN
            Long date=preferences.getLong("date",0);
            int expires=preferences.getInt("expires",0);
            Long now=System.currentTimeMillis()/1000;
            Long dateTokenExpires=date+expires;
            if(now>dateTokenExpires){
                //expired
            }else{
                //still available
            }
        }

        String url="https://www.skroutz.gr/oauth2/token";
        params = "client_id="+clientId;
        params = params+"&client_secret="+secret;
        params = params+"&grant_type="+grantType;
        params = params+"&scope="+scope;
        url=url+"?"+params;
        new GetToken().execute(url,"");




//        skroutzLoginButton= (Button) this.findViewById(R.id.skroutz);
//        skroutzLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent authIntent=new Intent(thisActivity,AuthActivity.class);
//                thisActivity.startActivity(authIntent);
//            }
//        });


    }

    class GetToken extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            return new Network(thisActivity).doPostRequest(thisActivity,params[0],params[1],false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("debug","response:"+s);
            try {
                JSONObject root=new JSONObject(s);
                String token=root.getString("access_token");
                int expiresSeconds=root.getInt("expires_in");
                SharedPreferences preferences=getSharedPreferences("credentials",MODE_PRIVATE);
                preferences.edit().putString("token",token).apply();
                preferences.edit().putInt("expires",expiresSeconds).apply();
                preferences.edit().putLong("date", System.currentTimeMillis()/1000).apply();
                Intent intent=new Intent(thisActivity,SearchActivity.class);
                thisActivity.startActivity(intent);
                thisActivity.finish();
            } catch (JSONException e) {

            }

        }
    }
}


