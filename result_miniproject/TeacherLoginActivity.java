package com.example.result_miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeacherLoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button loginButton, registerButton;
    TeacherDataHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);

        // Initialize database helper and SharedPreferences
        dbHelper = new TeacherDataHelper(this);
        sharedPreferences = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(TeacherLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check login in DB
                boolean isValid = dbHelper.checkTeacherLogin(username, password);

                if (isValid) {
                    // ✅ SAVE TEACHER USERNAME IN SHAREDPREFERENCES FOR PASSWORD CHANGE
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("teacher_username", username);
                    editor.apply();

                    Toast.makeText(TeacherLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TeacherLoginActivity.this, TeacherDashboardActivity.class);
                    intent.putExtra("teacher_username", username);
                    startActivity(intent);
                    finish();
                } else {
                    // If not found in DB
                    if (!dbHelper.isTeacherExists(username)) {
                        Toast.makeText(TeacherLoginActivity.this, "User not found! Please register first.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TeacherLoginActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(TeacherLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.isTeacherExists(username)) {
                    Toast.makeText(TeacherLoginActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean inserted = dbHelper.registerTeacher(username, password);
                    if (inserted) {
                        Toast.makeText(TeacherLoginActivity.this, "Registration successful! You can now login.", Toast.LENGTH_LONG).show();
                        // Clear fields after successful registration
                        etPassword.setText("");
                    } else {
                        Toast.makeText(TeacherLoginActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}