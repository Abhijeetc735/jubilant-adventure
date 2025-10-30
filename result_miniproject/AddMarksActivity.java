package com.example.result_miniproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddMarksActivity extends AppCompatActivity {

    EditText etRoll, etSubject, etMarks;
    Button btnSubmit;
    AddStudentDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        dbHelper = new AddStudentDB(this);

        etRoll = findViewById(R.id.etRoll);
        etSubject = findViewById(R.id.etSubject);
        etMarks = findViewById(R.id.etMarks);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarksToDatabase();
            }
        });
    }

    private void addMarksToDatabase() {
        String roll = etRoll.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String marks = etMarks.getText().toString().trim();

        // Validation
        if (roll.isEmpty() || subject.isEmpty() || marks.isEmpty()) {
            Toast.makeText(AddMarksActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if student exists
        if (!dbHelper.checkStudentExists(roll)) {
            Toast.makeText(AddMarksActivity.this, "Student with this roll number does not exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert marks
        boolean result = dbHelper.insertMarks(roll, subject, marks);

        if (result) {
            Toast.makeText(AddMarksActivity.this, "Marks Added Successfully", Toast.LENGTH_SHORT).show();
            // Clear fields
            etRoll.setText("");
            etSubject.setText("");
            etMarks.setText("");
        } else {
            Toast.makeText(AddMarksActivity.this, "Error Adding Marks!", Toast.LENGTH_SHORT).show();
        }
    }
}