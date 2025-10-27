package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {
    EditText etName, etRoll, etClass;
    Button btnSave;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etName = findViewById(R.id.etName);
        etRoll = findViewById(R.id.etRoll);
        etClass = findViewById(R.id.etClass);
        btnSave = findViewById(R.id.btnSave);

        // Open DB connection (same DB as teachers)
        TeacherDataHelper dbHelper = new TeacherDataHelper(this);
        db = dbHelper.getWritableDatabase();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String roll = etRoll.getText().toString().trim();
                String studentClass = etClass.getText().toString().trim();

                if (name.isEmpty() || roll.isEmpty() || studentClass.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert into student table
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("roll", roll);
                values.put("class", studentClass);

                long result = db.insert("students", null, values);
                if (result != -1) {
                    Toast.makeText(AddStudentActivity.this, "Student Added Successfully!", Toast.LENGTH_SHORT).show();
                    etName.setText("");
                    etRoll.setText("");
                    etClass.setText("");
                } else {
                    Toast.makeText(AddStudentActivity.this, "Error Adding Student!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


