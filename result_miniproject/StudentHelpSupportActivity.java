package com.example.result_miniproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentHelpSupportActivity extends AppCompatActivity {

    private EditText etStudentName, etContactNo, etProblemDescription;
    private Button btnSubmit, btnBack;
    private HelpSupportDB helpDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_help_support);

        // Initialize views
        etStudentName = findViewById(R.id.etStudentName);
        etContactNo = findViewById(R.id.etContactNo);
        etProblemDescription = findViewById(R.id.etProblemDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Initialize database
        helpDb = new HelpSupportDB(this);

        // Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHelpRequest();
            }
        });

        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to Student Dashboard
            }
        });
    }

    private void submitHelpRequest() {
        String studentName = etStudentName.getText().toString().trim();
        String contactNo = etContactNo.getText().toString().trim();
        String problemDescription = etProblemDescription.getText().toString().trim();

        // Simple validation
        if (studentName.isEmpty() || contactNo.isEmpty() || problemDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into database
        boolean success = helpDb.insertHelpRequest(studentName, contactNo, problemDescription);

        if (success) {
            Toast.makeText(this, "Help request submitted successfully!", Toast.LENGTH_LONG).show();
            clearForm();
        } else {
            Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etStudentName.setText("");
        etContactNo.setText("");
        etProblemDescription.setText("");
    }
}