
package com.bobo.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bobo.app.api.APIService;
import com.bobo.app.api.RetrofitClient;
import com.bobo.app.model.Child;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.Nullable;
import android.graphics.drawable.Drawable;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildSelectionActivity extends AppCompatActivity {

    private GridLayout childrenGrid;
    private TextView groupTitle;
    private String group;
    private String teacher;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_children_selection);

        childrenGrid = findViewById(R.id.childrenGrid);
        groupTitle = findViewById(R.id.groupTitle);

        group = getIntent().getStringExtra("group");
        teacher = getIntent().getStringExtra("teacher");
        password = getIntent().getStringExtra("password");

        groupTitle.setText(getString(R.string.group_select_child, group));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish();
                return true;
            }
            if (id == R.id.nav_camera) {
                Toast.makeText(this, "Please select a child first", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("username", teacher);
                intent.putExtra("password", password);
                intent.putExtra("role", "TEACHER");
                startActivity(intent);
                return true;
            }
            return false;
        });

        loadChildren();
    }

    private void loadChildren() {
        APIService apiService = RetrofitClient.getApiService();

        apiService.getChildrenByGroup(group).enqueue(new Callback<List<Child>>() {
            @Override
            public void onResponse(Call<List<Child>> call, Response<List<Child>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showChildren(response.body());
                } else {
                    Toast.makeText(ChildSelectionActivity.this,
                            "Не удалось загрузить детей", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Child>> call, Throwable t) {
                Toast.makeText(ChildSelectionActivity.this,
                        "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showChildren(List<Child> children) {
        childrenGrid.removeAllViews();
        for (Child child : children) {
            View childView = LayoutInflater.from(this).inflate(R.layout.item_child, childrenGrid, false);
            
            MaterialCardView cardView = (MaterialCardView) childView;
            ImageView imageView = childView.findViewById(R.id.childImageView);
            TextView nameView = childView.findViewById(R.id.childNameTextView);

            nameView.setText(child.getName());
            
            // Resolve image source
            Object imageSource = null;
            String imgPath = child.getImage();
            String name = child.getName();
            
            // Priority 1: Check if the 'image' field has a URL or drawable name
            if (imgPath != null && !imgPath.isEmpty()) {
                if (imgPath.startsWith("http")) {
                    imageSource = imgPath;
                } else {
                    String drawableName = imgPath.toLowerCase().trim().replace(" ", "_");
                    int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
                    if (resId != 0) {
                        imageSource = resId;
                    }
                }
            }
            
            // Priority 2: Fallback - try to match the child's NAME to a drawable (e.g., "Inzhu" -> inzhu.png)
            if (imageSource == null && name != null && !name.isEmpty()) {
                String nameAsDrawable = name.toLowerCase().trim().replace(" ", "_");
                int resId = getResources().getIdentifier(nameAsDrawable, "drawable", getPackageName());
                if (resId != 0) {
                    imageSource = resId;
                }
            }
            
            // Priority 3: Final fallback - relative path from server if imgPath exists
            if (imageSource == null && imgPath != null && !imgPath.isEmpty()) {
                imageSource = "http://192.168.8.123:8080/" + imgPath;
            }

            Log.d("ChildSelection", "Loading image for " + name + ": " + imageSource);

            Glide.with(this)
                    .load(imageSource)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ChildSelection", "Glide load failed for " + name + ": " + (e != null ? e.getMessage() : "unknown error"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("ChildSelection", "Glide load success for " + name);
                            return false;
                        }
                    })
                    .into(imageView);

            childView.setOnClickListener(v -> openCamera(child.getName()));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            childView.setLayoutParams(params);

            childrenGrid.addView(childView);
        }
    }

    private void openCamera(String child) {
        Intent intent = new Intent(ChildSelectionActivity.this, CameraActivity.class);
        intent.putExtra("child", child);
        intent.putExtra("teacher", teacher);
        startActivity(intent);
    }
}
