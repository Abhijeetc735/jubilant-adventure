package com.example.result_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentLoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    AddStudentDB dbHelper; // Use your main AddStudentDB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Register button removed from XML, so no need to find it

        // Initialize database helper - Use AddStudentDB instead of StudentDataHelper
        dbHelper = new AddStudentDB(this);

        // ---------------- LOGIN ONLY ----------------
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(StudentLoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isValid = dbHelper.checkStudentLogin(username, password);

                if (isValid) {
                    Toast.makeText(StudentLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
                    intent.putExtra("student_username", username);
                    startActivity(intent);
                    finish();
                } else {
                    // Check if student exists
                    if (!dbHelper.checkUsernameExists(username)) {
                        Toast.makeText(StudentLoginActivity.this, "Username not found! Please contact your teacher.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Register button click listener REMOVED
        // Teachers now add students with username/password through AddStudentActivity
    }
}