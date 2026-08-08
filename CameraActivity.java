package com.bobo.app;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import java.io.File;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bobo.app.api.APIService;
import com.bobo.app.api.RetrofitClient;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private PreviewView previewView;
    private ImageCapture imageCapture;

    private String child;
    private String teacher;
    private String mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        teacher = getIntent().getStringExtra("teacher");
        previewView = findViewById(R.id.previewView);

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> chooseMealType());
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted ->{
                    if(isGranted){
                       startCamera();
                    }else{
                        System.out.println("Camera permission denied");
                    }
                });
        if(ContextCompat.checkSelfPermission(
                this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startCamera();
        }else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        child = getIntent().getStringExtra("child");
        TextView text  = findViewById(R.id.childName);
        text.setText(child);
    }

    private void chooseMealType() {
        String[] meals = {"Breakfast", "Lunch", "Dinner"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose meal type")
                .setItems(meals, (dialog, which) -> {
                    mealType = meals[which];
                    takePhoto();
                })
                .show();
    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (Exception e) {
               e.printStackTrace();
            }
        },ContextCompat.getMainExecutor(this));
    }

    private void takePhoto(){
        if(imageCapture == null){
            return;
        }
        File photoFile = new File(
                getExternalFilesDir(null),
        System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                        intent.putExtra("photoPath", photoFile.getAbsolutePath());
                        intent.putExtra("child", child);
                        intent.putExtra("teacher", teacher);
                        intent.putExtra("mealType", mealType);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                            android.widget.Toast.makeText(
                                    CameraActivity.this,
                                    "Error: " + exception.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                    }
                }
        );
    }
}
