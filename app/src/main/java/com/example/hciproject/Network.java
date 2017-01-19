package com.example.hciproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.sql.CallableStatement;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Schoox on 1/19/2017.
 */

public class Network {


    private SharedPreferences preferences;
    private String token;
    Network(Activity activity){
        preferences=activity.getSharedPreferences("credentials",Activity.MODE_PRIVATE);
        token=preferences.getString("token","default");
    }

    boolean isTokenExpired(Activity activity){
        preferences=activity.getSharedPreferences("credentials",Activity.MODE_PRIVATE);
        if(!preferences.getString("token","default").equals("default")){
            //EXOUME ENA UPARXON TOKEN
            Long date=preferences.getLong("date",0);
            int expires=preferences.getInt("expires",0);
            Long now=System.currentTimeMillis()/1000;
            Long dateTokenExpires=date+expires;
            if(now>dateTokenExpires){
                //expired
                return true;
            }else{
                //still available
                return false;
            }
        }
        return true;
    }


    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = null;

    OkHttpClient getClient() {
        if(client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public String doGetRequest(Activity context, String url, boolean addAuthHeader) {
        Log.d("debug","url:"+url);
        Response response;
        try {
            Request request = buildRequest(context, url.replace("http:", "https:"), addAuthHeader, null);
            response = getClient().newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }catch (Exception e){
            return null;
        }

    }

    //Pass null for params for POST Request without body
    public String doPostRequest(Activity context, String url, String params, boolean addAuthHeader) {
        Log.d("debug","url:"+url);
        try {
            RequestBody requestBody = (params != null ? RequestBody.create(JSON, params) : RequestBody.create(null, new byte[0]));
            Request request = buildRequest(context, url.replace("http:", "https:"), addAuthHeader, requestBody);
            Response response;
            response = getClient().newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }catch (Exception e){
            return null;
        }
    }

    private Request buildRequest(Context context, String url, boolean addAuthHeader, RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if(addAuthHeader) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        if(requestBody != null) {
            requestBuilder.post(requestBody);
        }
        return requestBuilder.build();
    }


}
