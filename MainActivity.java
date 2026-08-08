package com.bobo.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String teacher;
    private String password;

    Button group1Button;
    Button group2Button;
    Button group3Button;
    Button group4Button;
    Button group5Button;
    Button group6Button;
    Button group7Button;
    Button group8Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        teacher = getIntent().getStringExtra("teacher");
        password = getIntent().getStringExtra("password");

        group1Button = findViewById(R.id.group1Button);
        group2Button = findViewById(R.id.group2Button);
        group3Button = findViewById(R.id.group3Button);
        group4Button = findViewById(R.id.group4Button);
        group5Button = findViewById(R.id.group5Button);
        group6Button = findViewById(R.id.group6Button);
        group7Button = findViewById(R.id.group7Button);

        group1Button.setOnClickListener(v -> openGroup("Group A"));
        group2Button.setOnClickListener(v -> openGroup("Group B"));
        group3Button.setOnClickListener(v -> openGroup("Group C"));
        group4Button.setOnClickListener(v -> openGroup("Group D"));
        group5Button.setOnClickListener(v -> openGroup("Group E"));
        group6Button.setOnClickListener(v -> openGroup("Group F"));
        group7Button.setOnClickListener(v -> openGroup("Group G"));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_camera) {
                // Open camera for a quick check or default child? 
                // Usually camera is opened after child selection, so maybe just toast for now
                Toast.makeText(this, "Select a child to take a photo!", Toast.LENGTH_SHORT).show();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openGroup(String group) {
        Intent intent = new Intent(MainActivity.this, ChildSelectionActivity.class);
        intent.putExtra("group", group);
        intent.putExtra("teacher", teacher);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}