/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.hciproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.List;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;

public class CameraActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor magnetSensor;
    private float magnetValues[] = new float[10];
    private float gyroValues[] = new float[10];
    private double magnetLastTime = 0;
    private double gyroLastTime = 0;

    private SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
//            if(System.currentTimeMillis()<gyroLastTime+500) return;
//            gyroLastTime = System.currentTimeMillis();
            gyroValues = event.values;
            String result = "gyro:";
            for (int i = 0; i < gyroValues.length; i++) {
                result = result + "###" + gyroValues[i];
            }
            Log.d("SENSOR", result);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("SENSOR:", "gyro accuracy:" + accuracy);

        }
    };
    private SensorEventListener magnetListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (System.currentTimeMillis() < magnetLastTime + 4000) return;
            magnetLastTime = System.currentTimeMillis();
            magnetValues = event.values;
            String result = "magnet:";
            for (int i = 0; i < magnetValues.length; i++) {
                result = result + "###" + magnetValues[i];
            }
            Log.d("SENSOR", result);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("SENSOR:", "magnet accuracy:" + accuracy);
        }
    };
    private List<Sensor> gyroList;
    private List<Sensor> magnetList;
    private CameraActivity thisActivity;
    private GoogleApiClient mGoogleApiClient;
    private double Xlat;
    private double Xlong;
    private int shopId;
    private String url;


    @Override
    protected void onStop() {
        super.onStop();
//        mSensorManager.unregisterListener(gyroListener);
        mSensorManager.unregisterListener(magnetListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        thisActivity = this;
        shopId = this.getIntent().getIntExtra("id", 0);
        url = "http://api.skroutz.gr/shops/" + shopId + "/locations";
        new GETLATLONG().execute(url);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = mSensorManager.getSensorList(TYPE_ACCELEROMETER).get(0);
        magnetSensor = mSensorManager.getSensorList(TYPE_MAGNETIC_FIELD).get(0);
        Log.d("SENSOR", magnetSensor == null ? "NO MAGNETOMETER" : "MAGNETOMETER SUCCESS");
        Log.d("SENSOR", gyroSensor == null ? "NO ACCELEROMETER" : "ACCELEROMETER SUCCESS");
//        mSensorManager.registerListener(gyroListener,gyroSensor,SENSOR_DELAY_UI);
        mSensorManager.registerListener(magnetListener, magnetSensor, SENSOR_DELAY_UI);

        int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            requestLocation();
        }

    }

    private void requestLocation() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



    }


    private LocationRequest mLocationRequest = new LocationRequest();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                Log.d("LOCATION", "Permission error");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("PLAY SERVICES", "Play services connected");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, thisActivity);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, thisActivity);
        } catch (Exception e) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOCATION", "lat:" + location.getLatitude() + "long:" + location.getLongitude());

        String result;
        if (location.getLatitude() < Xlat) {
            result = "PISW SOU";
            if (location.getLongitude() < Xlong) {
                result = result + " ARISTERA";
            } else {
                result = result + " DEKSIA";
            }
        } else {
            result = "MPROSTA SOU";
            if (location.getLongitude() < Xlong) {
                result = result + " ARISTERA";
            } else {
                result = result + " DEKSIA";
            }
        }

        Log.d("STORE", result);
    }

    class GETLATLONG extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            if (new Network(thisActivity).isTokenExpired(thisActivity)) {
                Intent intent = new Intent(thisActivity, MainActivity.class);
                thisActivity.startActivity(intent);
                thisActivity.finish();
            }
            String response = new Network(thisActivity).doGetRequest(thisActivity, params[0], true);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Xlat = Double.parseDouble(new JSONObject(s).getJSONArray("locations").getJSONObject(0).getString("lat"));
                Xlong = Double.parseDouble(new JSONObject(s).getJSONArray("locations").getJSONObject(0).getString("lng"));
                Log.d("SHOP", "lat:" + Xlat + "long:" + Xlong);
            } catch (Exception e) {
                Log.d("EXCEPTION", e.getLocalizedMessage());
            }

        }
    }
}
