package com.bobo.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bobo.app.adapter.PhotoAdapter;
import com.bobo.app.api.APIService;
import com.bobo.app.api.RetrofitClient;
import com.bobo.app.model.PhotoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private APIService apiService;
    private String username;
    private String password;
    private String role;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        String child = getIntent().getStringExtra("child");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        role = getIntent().getStringExtra("role");
        if (child == null || child.isEmpty()) {
            Log.e("ParentActivity", "Child is missing");
            finish();
            return;
        }
        apiService = RetrofitClient.getApiService();

        recyclerView = findViewById(R.id.recyclerPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView titleView = findViewById(R.id.title);
        titleView.setText(getString(R.string.child_food_diary, child));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_meals);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_meals) return true;
            if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("role", "PARENT");
                startActivity(intent);
                return true;
            }
            return false;
        });

        loadPhotos(child);
        Log.d("ParentActivity", "Loading photos for child: " + child);
    }

    private void loadPhotos(String child) {
        apiService.getPhotos(child).enqueue(new Callback<List<PhotoResponse>>() {

            @Override
            public void onResponse(Call<List<PhotoResponse>> call,
                                   Response<List<PhotoResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Log.d("ParentActivity", "Photos received: " + response.body().size());

                    PhotoAdapter adapter = new PhotoAdapter(response.body());
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.d("ParentActivity", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PhotoResponse>> call, Throwable t) {
                Log.e("ParentActivity", "Request failed", t);
            }
        });
    }
}