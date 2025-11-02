package com.example.result_miniproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnSubmit, btnBack;
    private AddStudentDB dbHelper;
    private String studentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_change_password);

        // Initialize views
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new AddStudentDB(this);

        // Get student username from intent
        studentUsername = getIntent().getStringExtra("student_username");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to Student Dashboard
            }
        });
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.equals(oldPassword)) {
            Toast.makeText(this, "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify old password first
        boolean isOldPasswordCorrect = dbHelper.checkStudentLogin(studentUsername, oldPassword);

        if (!isOldPasswordCorrect) {
            Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update password in database
        boolean success = dbHelper.updateStudentPassword(studentUsername, newPassword);

        if (success) {
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            finish(); // Go back to dashboard
        } else {
            Toast.makeText(this, "Failed to change password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etOldPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }
}