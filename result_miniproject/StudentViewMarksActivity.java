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
import java.text.DecimalFormat;

public class StudentViewMarksActivity extends AppCompatActivity {

    private TextView tvStudentInfo;
    private LinearLayout marksContainer;
    private Button btnBack;
    private AddStudentDB dbHelper;
    private String studentUsername;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_marks);

        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        marksContainer = findViewById(R.id.marksContainer);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new AddStudentDB(this);

        // Get student username from intent
        studentUsername = getIntent().getStringExtra("student_username");

        displayStudentMarks();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to StudentDashboard
            }
        });
    }

    private void displayStudentMarks() {
        // Clear previous marks
        marksContainer.removeAllViews();

        // Get student details (name, roll, class)
        Cursor studentCursor = dbHelper.getAllStudents();
        String studentName = "";
        String studentRoll = "";
        String studentClass = "";

        if (studentCursor != null && studentCursor.moveToFirst()) {
            do {
                String username = studentCursor.getString(studentCursor.getColumnIndexOrThrow("username"));
                if (username.equals(studentUsername)) {
                    studentName = studentCursor.getString(studentCursor.getColumnIndexOrThrow("name"));
                    studentRoll = studentCursor.getString(studentCursor.getColumnIndexOrThrow("roll"));
                    studentClass = studentCursor.getString(studentCursor.getColumnIndexOrThrow("class"));
                    break;
                }
            } while (studentCursor.moveToNext());
            studentCursor.close();
        }

        // Display student information
        if (!studentName.isEmpty()) {
            tvStudentInfo.setText("Student: " + studentName + " | Roll: " + studentRoll + " | Class: " + studentClass);
        } else {
            tvStudentInfo.setText("Student Information");
        }

        // Get marks for this student using roll number
        Cursor marksCursor = dbHelper.getMarksByRoll(studentRoll);
        boolean hasMarks = false;
        int totalSubjects = 0;
        int totalObtained = 0;
        int totalMaximum = 0;

        if (marksCursor != null && marksCursor.moveToFirst()) {
            hasMarks = true;

            // Create header
            createMarksHeader();

            do {
                String subject = marksCursor.getString(marksCursor.getColumnIndexOrThrow("subject"));
                String marksStr = marksCursor.getString(marksCursor.getColumnIndexOrThrow("marks_obtained"));

                // Parse marks (format: "obtained/total" or just marks)
                int obtained = 0;
                int total = 100; // Default total
                double percentage = 0;

                if (marksStr.contains("/")) {
                    String[] parts = marksStr.split("/");
                    obtained = Integer.parseInt(parts[0]);
                    total = Integer.parseInt(parts[1]);
                    percentage = (total > 0) ? (obtained * 100.0 / total) : 0;
                } else {
                    obtained = Integer.parseInt(marksStr);
                    percentage = (total > 0) ? (obtained * 100.0 / total) : 0;
                }

                // Create marks item
                View marksItem = createMarksItem(subject, obtained, total, percentage);
                marksContainer.addView(marksItem);

                totalSubjects++;
                totalObtained += obtained;
                totalMaximum += total;

            } while (marksCursor.moveToNext());
            marksCursor.close();

            // Add total row if there are marks
            if (totalSubjects > 0) {
                double overallPercentage = (totalMaximum > 0) ? (totalObtained * 100.0 / totalMaximum) : 0;
                addTotalRow(totalSubjects, totalObtained, totalMaximum, overallPercentage);
            }
        }

        if (!hasMarks) {
            showNoMarksMessage();
        }
    }

    private void createMarksHeader() {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        headerLayout.setLayoutParams(params);

        // Subject Header
        TextView tvSubjectHeader = createHeaderTextView("Subject", 1f);
        headerLayout.addView(tvSubjectHeader);

        // Marks Header
        TextView tvMarksHeader = createHeaderTextView("Marks", 0.6f);
        headerLayout.addView(tvMarksHeader);

        // Percentage Header
        TextView tvPercentageHeader = createHeaderTextView("Percentage", 0.6f);
        headerLayout.addView(tvPercentageHeader);

        marksContainer.addView(headerLayout);
    }

    private TextView createHeaderTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(14);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setPadding(16, 12, 16, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight
        );
        textView.setLayoutParams(params);

        return textView;
    }

    private View createMarksItem(String subject, int obtained, int total, double percentage) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        itemLayout.setLayoutParams(params);

        // Subject
        TextView tvSubject = createNormalTextView(subject, 1f);
        itemLayout.addView(tvSubject);

        // Marks (Obtained/Total)
        TextView tvMarks = createNormalTextView(obtained + "/" + total, 0.6f);
        itemLayout.addView(tvMarks);

        // Percentage
        DecimalFormat df = new DecimalFormat("#.##");
        TextView tvPercentage = createNormalTextView(df.format(percentage) + "%", 0.6f);

        // Color code based on percentage
        if (percentage >= 75) {
            tvPercentage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (percentage >= 50) {
            tvPercentage.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvPercentage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        itemLayout.addView(tvPercentage);

        return itemLayout;
    }

    private TextView createNormalTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setPadding(16, 12, 16, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight
        );
        textView.setLayoutParams(params);

        return textView;
    }

    private void addTotalRow(int totalSubjects, int totalObtained, int totalMaximum, double overallPercentage) {
        LinearLayout totalLayout = new LinearLayout(this);
        totalLayout.setOrientation(LinearLayout.HORIZONTAL);
        totalLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 0);
        totalLayout.setLayoutParams(params);

        // Total Label
        TextView tvTotalLabel = createHeaderTextView("TOTAL", 1f);
        tvTotalLabel.setTextColor(getResources().getColor(android.R.color.black));
        totalLayout.addView(tvTotalLabel);

        // Total Marks
        TextView tvTotalMarks = createHeaderTextView(totalObtained + "/" + totalMaximum, 0.6f);
        tvTotalMarks.setTextColor(getResources().getColor(android.R.color.black));
        totalLayout.addView(tvTotalMarks);

        // Overall Percentage
        DecimalFormat df = new DecimalFormat("#.##");
        TextView tvOverallPercentage = createHeaderTextView(df.format(overallPercentage) + "%", 0.6f);
        tvOverallPercentage.setTextColor(getResources().getColor(android.R.color.black));
        totalLayout.addView(tvOverallPercentage);

        marksContainer.addView(totalLayout);
    }

    private void showNoMarksMessage() {
        TextView tvNoMarks = new TextView(this);
        tvNoMarks.setText("No marks available yet.\nYour teacher will add your marks soon.");
        tvNoMarks.setTextSize(16);
        tvNoMarks.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvNoMarks.setPadding(0, 40, 0, 0);
        tvNoMarks.setGravity(View.TEXT_ALIGNMENT_CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvNoMarks.setLayoutParams(params);

        marksContainer.addView(tvNoMarks);
    }
}