package com.example.result_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class StudentLoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button loginButton, registerButton;

    StudentDataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);

        // Initialize database helper
        dbHelper = new StudentDataHelper(this);

        // ---------------- LOGIN ----------------
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(StudentLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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
                    if (!dbHelper.isStudentExists(username)) {
                        Toast.makeText(StudentLoginActivity.this, "User not found! Please register first.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // ---------------- REGISTER ----------------
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(StudentLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.isStudentExists(username)) {
                    Toast.makeText(StudentLoginActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean inserted = dbHelper.insertStudent(username, password);
                    if (inserted) {
                        Toast.makeText(StudentLoginActivity.this, "Registration successful! You can now login.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
