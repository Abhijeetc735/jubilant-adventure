package com.example.result_miniproject;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddMarksActivity extends AppCompatActivity {
    EditText etRoll, etSubject, etMarks;
    Button btnSubmit;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        etRoll = findViewById(R.id.etRoll);
        etSubject = findViewById(R.id.etSubject);
        etMarks = findViewById(R.id.etMarks);
        btnSubmit = findViewById(R.id.btnSubmit);

        TeacherDataHelper dbHelper = new TeacherDataHelper(this);
        db = dbHelper.getWritableDatabase();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
        String roll = etRoll.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String marks = etMarks.getText().toString().trim();

        if (roll.isEmpty() || subject.isEmpty() || marks.isEmpty()) {
            Toast.makeText(AddMarksActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("roll", roll);
        cv.put("subject", subject);
        cv.put("marks", marks);

        long result = db.insert("marks", null, cv);
        if (result != -1) {
            Toast.makeText(AddMarksActivity.this, "Marks Added Successfully!", Toast.LENGTH_SHORT).show();
            etRoll.setText("");
            etSubject.setText("");
            etMarks.setText("");
        } else {
            Toast.makeText(AddMarksActivity.this, "Error Adding Marks!", Toast.LENGTH_SHORT).show();
        }
    }
});
        }
        }



