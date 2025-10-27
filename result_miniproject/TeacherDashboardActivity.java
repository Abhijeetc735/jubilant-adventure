package com.example.result_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    // Declare buttons
    Button btnAddStudent, btnAddMarks, btnViewMarks, btnReports, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Initialize buttons
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddMarks = findViewById(R.id.btnAddMarks);
        btnViewMarks = findViewById(R.id.btnViewMarks);
        btnReports = findViewById(R.id.btnReports);
        btnSettings = findViewById(R.id.btnSettings);

        // Navigate to Add Student
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherDashboardActivity.this, AddStudentActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Add Marks
        btnAddMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherDashboardActivity.this, AddMarksActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to View Marks
        btnViewMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherDashboardActivity.this, ViewMarksActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Reports
        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherDashboardActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Settings
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherDashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
