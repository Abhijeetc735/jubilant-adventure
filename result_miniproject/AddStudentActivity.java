package com.example.result_miniproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    EditText etName, etRoll, etClass, etUsername, etPassword;
    Button btnSave;
    AddStudentDB studentDbHelper; // ✅ Using new DB

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etName = findViewById(R.id.etName);
        etRoll = findViewById(R.id.etRoll);
        etClass = findViewById(R.id.etClass);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);

        // ✅ Initialize new student database
        studentDbHelper = new AddStudentDB(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String roll = etRoll.getText().toString().trim();
                String studentClass = etClass.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (name.isEmpty() || roll.isEmpty() || studentClass.isEmpty()
                        || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this,
                            "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = studentDbHelper.insertStudent(
                        name, roll, studentClass, username, password
                );

                if (success) {
                    Toast.makeText(AddStudentActivity.this,
                            "Student added successfully!", Toast.LENGTH_SHORT).show();
                    etName.setText("");
                    etRoll.setText("");
                    etClass.setText("");
                    etUsername.setText("");
                    etPassword.setText("");
                } else {
                    Toast.makeText(AddStudentActivity.this,
                            "Student already exists or error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
