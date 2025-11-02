package com.example.result_miniproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReportsActivity extends AppCompatActivity {

    private Button btnGenerateReports, btnViewAllStudents, btnBack;
    private LinearLayout reportsContainer;
    private TextView tvNoData;
    private AddStudentDB dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dbHelper = new AddStudentDB(this);

        // Initialize views
        btnGenerateReports = findViewById(R.id.btnGenerateReports);
        btnViewAllStudents = findViewById(R.id.btnViewAllStudents);
        btnBack = findViewById(R.id.btnBack);
        reportsContainer = findViewById(R.id.reportsContainer);
        tvNoData = findViewById(R.id.tvNoData);

        setupButtonListeners();
        generateReports(); // Show reports by default
    }

    private void setupButtonListeners() {
        btnGenerateReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateReports();
            }
        });

        btnViewAllStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllStudents();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void generateReports() {
        reportsContainer.removeAllViews();

        Cursor studentsCursor = dbHelper.getAllStudents();
        boolean hasData = false;

        if (studentsCursor != null && studentsCursor.moveToFirst()) {
            hasData = true;

            do {
                String roll = studentsCursor.getString(studentsCursor.getColumnIndexOrThrow("roll"));
                String name = studentsCursor.getString(studentsCursor.getColumnIndexOrThrow("name"));
                String studentClass = studentsCursor.getString(studentsCursor.getColumnIndexOrThrow("class"));

                // Get marks for this student
                Cursor marksCursor = dbHelper.getMarksByRoll(roll);
                int totalSubjects = 0;
                int totalMarks = 0;
                int totalObtained = 0;

                if (marksCursor != null && marksCursor.moveToFirst()) {
                    do {
                        String marksStr = marksCursor.getString(marksCursor.getColumnIndexOrThrow("marks_obtained"));

                        // Parse marks
                        if (marksStr.contains("/")) {
                            String[] parts = marksStr.split("/");
                            int obtained = Integer.parseInt(parts[0]);
                            int total = Integer.parseInt(parts[1]);
                            totalObtained += obtained;
                            totalMarks += total;
                        } else {
                            int obtained = Integer.parseInt(marksStr);
                            totalObtained += obtained;
                            totalMarks += 100; // Assume total is 100
                        }
                        totalSubjects++;

                    } while (marksCursor.moveToNext());
                    marksCursor.close();
                }

                // Calculate percentage and grade
                double percentage = (totalMarks > 0) ? (totalObtained * 100.0 / totalMarks) : 0;
                String grade = calculateGrade(percentage);

                // Create report item
                View reportItem = createReportItem(roll, name, studentClass, totalSubjects, totalObtained, totalMarks, percentage, grade);
                reportsContainer.addView(reportItem);

            } while (studentsCursor.moveToNext());

            studentsCursor.close();
        }

        showData(hasData);

        if (hasData) {
            Toast.makeText(this, "Reports generated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewAllStudents() {
        reportsContainer.removeAllViews();

        Cursor cursor = dbHelper.getAllStudents();
        boolean hasData = false;

        if (cursor != null && cursor.moveToFirst()) {
            hasData = true;

            do {
                String roll = cursor.getString(cursor.getColumnIndexOrThrow("roll"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String studentClass = cursor.getString(cursor.getColumnIndexOrThrow("class"));

                View studentItem = createStudentItem(roll, name, studentClass);
                reportsContainer.addView(studentItem);

            } while (cursor.moveToNext());

            cursor.close();
        }

        showData(hasData);

        if (hasData) {
            Toast.makeText(this, "All students displayed", Toast.LENGTH_SHORT).show();
        }
    }

    private View createReportItem(String roll, String name, String studentClass,
                                  int totalSubjects, int totalObtained, int totalMarks,
                                  double percentage, String grade) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(40, 25, 40, 25);
        itemLayout.setBackgroundResource(R.drawable.item_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(params);

        // Student Info
        TextView tvStudent = new TextView(this);
        tvStudent.setText("Student: " + name + " (Roll: " + roll + ")");
        tvStudent.setTextSize(18);
        tvStudent.setTextColor(getResources().getColor(android.R.color.black));
        tvStudent.setTypeface(null, android.graphics.Typeface.BOLD);
        itemLayout.addView(tvStudent);

        // Class
        TextView tvClass = new TextView(this);
        tvClass.setText("Class: " + studentClass);
        tvClass.setTextSize(14);
        tvClass.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvClass.setPadding(0, 4, 0, 0);
        itemLayout.addView(tvClass);

        // Marks Summary
        TextView tvMarks = new TextView(this);
        if (totalSubjects > 0) {
            tvMarks.setText("Subjects: " + totalSubjects + " | Total Marks: " + totalObtained + "/" + totalMarks);
        } else {
            tvMarks.setText("No marks recorded");
        }
        tvMarks.setTextSize(14);
        tvMarks.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvMarks.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvMarks);

        // Percentage and Grade
        if (totalSubjects > 0) {
            TextView tvPercentage = new TextView(this);
            tvPercentage.setText("Percentage: " + String.format("%.1f%%", percentage) + " | Grade: " + grade);
            tvPercentage.setTextSize(14);
            tvPercentage.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvPercentage.setPadding(0, 4, 0, 0);
            itemLayout.addView(tvPercentage);

            // Color based on percentage
            if (percentage >= 75) {
                itemLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else if (percentage >= 50) {
                itemLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            } else if (percentage > 0) {
                itemLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }
        }

        return itemLayout;
    }

    private View createStudentItem(String roll, String name, String studentClass) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(40, 25, 40, 25);
        itemLayout.setBackgroundResource(R.drawable.item_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(params);

        // Student Name
        TextView tvName = new TextView(this);
        tvName.setText("Student: " + name);
        tvName.setTextSize(18);
        tvName.setTextColor(getResources().getColor(android.R.color.black));
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        itemLayout.addView(tvName);

        // Roll Number and Class
        TextView tvDetails = new TextView(this);
        tvDetails.setText("Roll: " + roll + " | Class: " + studentClass);
        tvDetails.setTextSize(14);
        tvDetails.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvDetails.setPadding(0, 4, 0, 0);
        itemLayout.addView(tvDetails);

        return itemLayout;
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    private void showData(boolean hasData) {
        if (hasData) {
            reportsContainer.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            reportsContainer.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }
}