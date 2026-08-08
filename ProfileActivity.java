package com.bobo.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView userNameText = findViewById(R.id.userNameText);
        TextView userRoleText = findViewById(R.id.userRoleText);
        TextView userPasswordText = findViewById(R.id.userPasswordText);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String role = getIntent().getStringExtra("role");

        userNameText.setText(getString(R.string.hello_user, username));
        userRoleText.setText(getString(R.string.role_format, role));
        userPasswordText.setText(getString(R.string.password_format, password));

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}