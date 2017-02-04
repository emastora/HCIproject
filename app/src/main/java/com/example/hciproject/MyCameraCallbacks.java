package com.example.hciproject;

import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.util.Log;

/**
 * Created by Schoox on 2/4/2017.
 */

public class MyCameraCallbacks extends CameraDevice.StateCallback {

    private OnCameraStateChangedListener listener;

    MyCameraCallbacks(OnCameraStateChangedListener listener){
        this.listener=listener;
    }

    private CameraDevice camera;
    @Override
    public void onOpened(CameraDevice camera) {
        Log.d("Camera","openeds");
        this.camera=camera;
        listener.cameraAvailable(camera);
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
        Log.d("Camera","onDisconnected");
        listener.cameraAvailable(camera);

    }

    @Override
    public void onError(CameraDevice camera, int error) {
        Log.d("Camera","onError");
        listener.cameraAvailable(camera);

    }



    public interface OnCameraStateChangedListener{
        void cameraAvailable(CameraDevice camera);
    }
}
