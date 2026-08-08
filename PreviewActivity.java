package com.bobo.app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ImageView imageView = findViewById(R.id.photoPreview);
        Button btnAnotherChild = findViewById(R.id.btnAnotherChild);
        Button btnLogout = findViewById(R.id.btnLogout);

        String path = getIntent().getStringExtra("photoPath");
        String child = getIntent().getStringExtra("child");
        String teacher = getIntent().getStringExtra("teacher");
        String mealType = getIntent().getStringExtra("mealType");
        
        if (path != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }

        btnAnotherChild.setOnClickListener(v -> {
            if (path != null) {
                uploadPhoto(new File(path), child, teacher, mealType, "ANOTHER");
            } else {
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            if (path != null) {
                uploadPhoto(new File(path), child, teacher, mealType, "LOGOUT");
            } else {
                navigateToLogin();
            }
        });
    }

    private void uploadPhoto(File photoFile, String child, String teacher, String mealType, String target) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.8.123:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        APIService service = retrofit.create(APIService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", photoFile.getName(), requestFile);
        RequestBody childBody = RequestBody.create(MediaType.parse("text/plain"), child);
        RequestBody teacherBody = RequestBody.create(MediaType.parse("text/plain"), teacher);
        RequestBody mealTypeBody = RequestBody.create(MediaType.parse("text/plain"), mealType);

        Call<ResponseBody> call = service.uploadPhoto(body, childBody, teacherBody, mealTypeBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PreviewActivity.this, "Meal sent successfully! 🎉", Toast.LENGTH_SHORT).show();
                    if ("LOGOUT".equals(target)) {
                        navigateToLogin();
                    } else {
                        finish(); // Returns to ChildSelection
                    }
                } else {
                    Toast.makeText(PreviewActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PreviewActivity.this, "Error sending meal. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(PreviewActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public interface APIService {
        @retrofit2.http.Multipart
        @retrofit2.http.POST("upload")
        Call<ResponseBody> uploadPhoto(
                @retrofit2.http.Part MultipartBody.Part photo,
                @retrofit2.http.Part("child") RequestBody child,
                @retrofit2.http.Part("teacher") RequestBody teacher,
                @retrofit2.http.Part("mealType") RequestBody mealType
        );
    }
}