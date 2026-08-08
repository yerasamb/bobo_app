package com.bobo.app;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView imageView = findViewById(R.id.fullImageView);
        ImageButton btnClose = findViewById(R.id.btnClose);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);

        btnClose.setOnClickListener(v -> finish());
    }
}