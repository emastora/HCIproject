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
import android.hardware.GeomagneticField;
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
import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class CameraActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor magnetSensor;
    private float magnetValues[] = new float[10];
    private float gyroValues[] = new float[10];
    private float gravity[] = new float[3];
    private double magnetLastTime = 0;
    private double gyroLastTime = 0;
    // Rotation data
    private float[] rotation = new float[9];
    // orientation (azimuth, pitch, roll)
    private float[] orientation = new float[3];

    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies. - See more at: https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings#sthash.ztD9m6a8.dpuf

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private float[] smoothed;
    private float[] geomagnetic = new float[3];
    private boolean accelOrMagnetic;
    private float bearing;
    private SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            try {
                // get accelerometer data
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    // we need to use a low pass filter to make data smoothed
                    smoothed = lowPass(event.values, gravity);
                    gravity[0] = smoothed[0];
                    gravity[1] = smoothed[1];
                    gravity[2] = smoothed[2];
                    accelOrMagnetic = true;

                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    smoothed = lowPass(event.values, geomagnetic);
                    geomagnetic[0] = smoothed[0];
                    geomagnetic[1] = smoothed[1];
                    geomagnetic[2] = smoothed[2];
                    accelOrMagnetic = true;

                }

                // get rotation matrix to get gravity and magnetic data
                SensorManager.getRotationMatrix(rotation, null, gravity, geomagnetic);
                // get bearing to target
                SensorManager.getOrientation(rotation, orientation);
                // east degrees of true North
                bearing = orientation[0];
                // convert from radians to degrees
                bearing = (float) Math.toDegrees(bearing);

                // fix difference between true North and magnetical North
                if (geomagneticField != null) {
                    bearing += geomagneticField.getDeclination();
                }

                // bearing must be in 0-360
                if (bearing < 0) {
                    bearing += 360;
                }

                if (accelOrMagnetic)
                    updateTextDirection(bearing); // display text direction on screen
            } catch (Exception e) {

            }
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
    private Location currentLocation;
    private GeomagneticField geomagneticField;

    private void updateTextDirection(double bearing) {
        int range = (int) (bearing / (360f / 16f));
        String dirTxt = "";

        if (range == 15 || range == 0)
            dirTxt = "N";
        if (range == 1 || range == 2)
            dirTxt = "NE";
        if (range == 3 || range == 4)
            dirTxt = "E";
        if (range == 5 || range == 6)
            dirTxt = "SE";
        if (range == 7 || range == 8)
            dirTxt = "S";
        if (range == 9 || range == 10)
            dirTxt = "SW";
        if (range == 11 || range == 12)
            dirTxt = "W";
        if (range == 13 || range == 14)
            dirTxt = "NW";

        if(bearing<moires && bearing>moires-90){
            Log.d("RESULT","MPAM");
            if(fragment!=null){
                fragment.setColor(true);

            }

        }else{
            if(fragment!=null){
                fragment.setColor(false);
            }
        }
        Log.d("RESULT: ", "" + ((int) bearing) + ((char) 176) + " "
                + dirTxt); // char 176 ) = degrees ...
    }


    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(gyroListener);
        mSensorManager.unregisterListener(magnetListener);
    }

    Camera2VideoFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        thisActivity = this;
        shopId = this.getIntent().getIntExtra("id", 0);
        url = "http://api.skroutz.gr/shops/" + shopId + "/locations";
        new GETLATLONG().execute(url);
        if (null == savedInstanceState) {
            fragment=Camera2VideoFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,fragment )
                    .commit();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = mSensorManager.getSensorList(TYPE_ACCELEROMETER).get(0);
        magnetSensor = mSensorManager.getSensorList(TYPE_MAGNETIC_FIELD).get(0);
        Log.d("SENSOR", magnetSensor == null ? "NO MAGNETOMETER" : "MAGNETOMETER SUCCESS");
        Log.d("SENSOR", gyroSensor == null ? "NO ACCELEROMETER" : "ACCELEROMETER SUCCESS");
        mSensorManager.registerListener(gyroListener, gyroSensor, SENSOR_DELAY_UI);
        mSensorManager.registerListener(gyroListener, magnetSensor, SENSOR_DELAY_UI);

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
        } else {
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
            mGoogleApiClient.connect();
        }


    }


    private LocationRequest mLocationRequest = new LocationRequest();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("PLAY SERVICES", "Play services connected");
        try {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(PRIORITY_BALANCED_POWER_ACCURACY);
            FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, thisActivity);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            processLocation(location);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        FusedLocationApi.removeLocationUpdates(mGoogleApiClient, thisActivity);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            FusedLocationApi.removeLocationUpdates(mGoogleApiClient, thisActivity);
        } catch (Exception e) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        processLocation(location);

    }

    int moires;

    private void processLocation(Location location) {
        Log.d("LOCATION", "lat:" + location.getLatitude() + "long:" + location.getLongitude());

        String result;
        if (Xlat > location.getLatitude()) {
            //0-180
            if (Xlong > location.getLongitude()) {
                //0-90
                moires=90;
            } else {
                //90-180
                moires=180;
            }
        } else {
            //180-360
            if (Xlong > location.getLongitude()) {
                //180-270
                moires=270;
            } else {
                //270-360
                moires=360;
            }
        }

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

        currentLocation = location;
        geomagneticField = new GeomagneticField(
                (float) currentLocation.getLatitude(),
                (float) currentLocation.getLongitude(),
                (float) currentLocation.getAltitude(),
                System.currentTimeMillis());


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
