package com.example.result_miniproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherViewHelpActivity extends AppCompatActivity {

    private LinearLayout helpRequestsContainer;
    private Button btnBack;
    private HelpSupportDB helpDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_help);

        helpRequestsContainer = findViewById(R.id.helpRequestsContainer);
        btnBack = findViewById(R.id.btnBack);
        helpDb = new HelpSupportDB(this);

        displayAllHelpRequests();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to Teacher Dashboard
            }
        });
    }

    private void displayAllHelpRequests() {
        helpRequestsContainer.removeAllViews();

        Cursor cursor = helpDb.getAllHelpRequests();

        if (cursor != null && cursor.moveToFirst()) {
            int requestCount = 0;

            do {
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                String contactNo = cursor.getString(cursor.getColumnIndexOrThrow("contact_no"));
                String problemDescription = cursor.getString(cursor.getColumnIndexOrThrow("problem_description"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

                requestCount++;
                View helpItem = createHelpRequestItem(requestCount, studentName, contactNo, problemDescription, timestamp);
                helpRequestsContainer.addView(helpItem);

            } while (cursor.moveToNext());
            cursor.close();

            Toast.makeText(this, "Found " + requestCount + " help requests", Toast.LENGTH_SHORT).show();
        } else {
            showNoRequestsMessage();
        }
    }

    private View createHelpRequestItem(int number, String studentName, String contactNo, String problem, String timestamp) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(30, 20, 30, 20);
        itemLayout.setBackgroundResource(R.drawable.item_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(params);

        // Request Number and Student Name
        TextView tvStudent = new TextView(this);
        tvStudent.setText(number + ". 👤 " + studentName);
        tvStudent.setTextSize(16);
        tvStudent.setTextColor(getResources().getColor(android.R.color.black));
        tvStudent.setTypeface(null, android.graphics.Typeface.BOLD);
        itemLayout.addView(tvStudent);

        // Contact Number
        TextView tvContact = new TextView(this);
        tvContact.setText("📞 Contact: " + contactNo);
        tvContact.setTextSize(14);
        tvContact.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvContact.setPadding(0, 4, 0, 0);
        itemLayout.addView(tvContact);

        // Problem Description
        TextView tvProblem = new TextView(this);
        tvProblem.setText("📝 Problem: " + problem);
        tvProblem.setTextSize(14);
        tvProblem.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvProblem.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvProblem);

        // Timestamp
        TextView tvTime = new TextView(this);
        tvTime.setText("🕒 Submitted: " + timestamp);
        tvTime.setTextSize(12);
        tvTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvTime.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvTime);

        return itemLayout;
    }

    private void showNoRequestsMessage() {
        TextView tvNoRequests = new TextView(this);
        tvNoRequests.setText("No help requests from students yet.\nStudents can submit requests from their dashboard.");
        tvNoRequests.setTextSize(16);
        tvNoRequests.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvNoRequests.setPadding(0, 40, 0, 0);
        tvNoRequests.setGravity(View.TEXT_ALIGNMENT_CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvNoRequests.setLayoutParams(params);

        helpRequestsContainer.addView(tvNoRequests);
    }
}