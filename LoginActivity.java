package com.bobo.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.bobo.app.api.APIService;
import com.bobo.app.api.RetrofitClient;
import com.bobo.app.model.LoginRequest;
import com.bobo.app.model.LoginResponse;
import com.bobo.app.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button buttonLogin;
    private EditText resetUsername;
    private EditText resetPassword;
    private EditText resetConfirmPassword;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        TextView btnResetPassword = findViewById(R.id.btnResetPassword);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        apiService = RetrofitClient.getApiService();

        findViewById(R.id.langKK).setOnClickListener(v -> setLocale("kz"));
        findViewById(R.id.langRU).setOnClickListener(v -> setLocale("ru"));
        findViewById(R.id.langEN).setOnClickListener(v -> setLocale("en"));

        buttonLogin.setOnClickListener(v -> login());
        btnResetPassword.setOnClickListener(v -> showResetPasswordDialog());
        btnCreateAccount.setOnClickListener(v -> showRegisterDialog());
    }

    private void showResetPasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        resetUsername = new EditText(this);
        resetUsername.setHint("Username");
        layout.addView(resetUsername, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        resetPassword = new EditText(this);
        resetPassword.setHint("New password");
        resetPassword.setInputType(0x81);
        layout.addView(resetPassword, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        resetConfirmPassword = new EditText(this);
        resetConfirmPassword.setHint("Confirm new password");
        resetConfirmPassword.setInputType(0x81);
        layout.addView(resetConfirmPassword, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (dialog, which) -> resetPassword((AlertDialog) dialog))
                .show();
    }

    private void resetPassword(AlertDialog dialog) {
        String username = resetUsername.getText().toString().trim();
        String password = resetPassword.getText().toString();
        String confirmPassword = resetConfirmPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("newPassword", password);

        apiService.resetPassword(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Password reset successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLocale(String languageCode) {
        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    private void login() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(username, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();

                    if ("PARENT".equals(user.getRole())) {
                        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
                        intent.putExtra("child", user.getChild());
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("password", password);
                        intent.putExtra("role", "PARENT");
                        startActivity(intent);
                        finish();
                    } else if ("TEACHER".equals(user.getRole())) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("teacher", user.getUsername());
                        intent.putExtra("password", password);
                        intent.putExtra("role", "TEACHER");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRegisterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        
        EditText usernameInput = dialogView.findViewById(R.id.regUsername);
        EditText passwordInput = dialogView.findViewById(R.id.regPassword);
        EditText confirmInput = dialogView.findViewById(R.id.regConfirmPassword);
        EditText childNameInput = dialogView.findViewById(R.id.regChildName);
        android.widget.Spinner roleSpinner = dialogView.findViewById(R.id.regRoleSpinner);
        View childContainer = dialogView.findViewById(R.id.regChildContainer);
        Button btnCreate = dialogView.findViewById(R.id.regBtnCreate);
        Button btnCancel = dialogView.findViewById(R.id.regBtnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_App_KidMode)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Roles setup
        String[] roles = {"PARENT", "TEACHER"};
        android.widget.ArrayAdapter<String> roleAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                childContainer.setVisibility("PARENT".equals(roles[position]) ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnCreate.setOnClickListener(v -> {
            String userTxt = usernameInput.getText().toString().trim();
            String passTxt = passwordInput.getText().toString();
            String confTxt = confirmInput.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();
            String child = "PARENT".equals(role) ? childNameInput.getText().toString().trim() : null;

            if (userTxt.isEmpty() || passTxt.isEmpty() || confTxt.isEmpty() || ("PARENT".equals(role) && child.isEmpty())) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!passTxt.equals(confTxt)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.register(new User(userTxt, passTxt, role, child)).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Account created!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
