package com.example.result_miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RoleSelectionActivity extends AppCompatActivity {

    Button buttonTeacher, buttonStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_role_selection);

        buttonStudent=findViewById(R.id.buttonStudent);
        buttonTeacher=findViewById(R.id.buttonTeacher);

        buttonTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to LoginActivity and pass role info
                Intent intent = new Intent(RoleSelectionActivity.this, TeacherLoginActivity.class);
                intent.putExtra("role", "Teacher");
                startActivity(intent);
            }
        });

        buttonStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to LoginActivity and pass role info
                Intent intent = new Intent(RoleSelectionActivity.this, StudentLoginActivity.class);
                intent.putExtra("role", "Student");
                startActivity(intent);
            }
        });




    }
}