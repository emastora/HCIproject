package com.example.hciproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, MyCameraCallbacks.OnCameraStateChangedListener {

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private CoordinatorLayout coordinatorLayout;
    private CameraActivity thisActivity;
    private MyCameraCallbacks cameraHelper;
    private int surfaceFormat;
    private int surfaceWidth;
    private int surfaceHeight;
    private CameraCaptureSession mCaptureSession;
    private CopyOnWriteArrayList<Integer> mPreviewRequestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        thisActivity = this;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSurfaceView = (SurfaceView) findViewById(R.id.camera);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        requestCamera();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Surface", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Surface", "surfaceChanged");
        surfaceFormat = format;
        surfaceWidth = width;
        surfaceHeight = height;


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Surface", "surfaceDestroyed");
    }

    private boolean requestCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIds;
        try {
            cameraIds = cameraManager.getCameraIdList();
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(thisActivity,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraHelper = new MyCameraCallbacks(thisActivity);
                cameraManager.openCamera(cameraIds[0], cameraHelper, new Handler());
                return true;
            } else {
                Log.d("Camera", "you don't have the permission");
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("Camera", "camera not available");
        }
        return false;
    }


    @Override
    public void cameraAvailable(final CameraDevice camera) {
        if (camera == null) {
            Log.d("Camera", "camera null");
        }
        try {
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(mSurfaceView.getHolder().getSurface());
            camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // The camera is already closed
                    if (null == camera) {
                        return;
                    }

                    // When the session is ready, we start displaying the preview.
                    mCaptureSession = cameraCaptureSession;
                    Log.d("Session","created");
                }

                @Override
                public void onConfigureFailed(
                        @NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.d("Session", "onConfigureFailed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("Camera", "cannot create session");
        }
    }
}
