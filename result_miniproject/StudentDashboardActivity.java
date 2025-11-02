package com.example.result_miniproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentDashboardActivity extends Activity {

    TextView tvWelcome;
    Button btnViewMarks, btnChangePassword, btnHelpSupport, btnLogout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewMarks = findViewById(R.id.btnViewMarks);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnHelpSupport = findViewById(R.id.btnHelpSupport);
        btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("student_username");
        tvWelcome.setText("Welcome, " + username);

        // View My Marks Button
        btnViewMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashboardActivity.this, StudentViewMarksActivity.class);
                intent.putExtra("student_username", username);
                startActivity(intent);
            }
        });

        // Change Password Button
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashboardActivity.this, StudentChangePasswordActivity.class);
                intent.putExtra("student_username", username);
                startActivity(intent);
            }
        });

        // Help & Support Button
        btnHelpSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashboardActivity.this, StudentHelpSupportActivity.class);
                startActivity(intent);
            }
        });
// In StudentDashboardActivity onCreate method:
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashboardActivity.this, StudentChangePasswordActivity.class);
                intent.putExtra("student_username", username);
                startActivity(intent);
            }
        });
        // Logout Button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDashboardActivity.this, StudentLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}