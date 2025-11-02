package com.example.result_miniproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ViewMarksActivity extends AppCompatActivity {

    private EditText etSearchRoll;
    private Button btnSearch, btnViewAll, btnBack;
    private TableLayout tableMarks;
    private LinearLayout noDataLayout;
    private AddStudentDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);

        dbHelper = new AddStudentDB(this);

        // Initialize views
        etSearchRoll = findViewById(R.id.etSearchRoll);
        btnSearch = findViewById(R.id.btnSearch);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnBack = findViewById(R.id.btnBack);
        tableMarks = findViewById(R.id.tableMarks);
        noDataLayout = findViewById(R.id.noDataLayout);

        setupButtonListeners();
        displayAllMarks(); // Show all marks by default
    }

    private void setupButtonListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMarksByRoll();
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAllMarks();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchMarksByRoll() {
        String roll = etSearchRoll.getText().toString().trim();

        if (roll.isEmpty()) {
            Toast.makeText(this, "Please enter roll number", Toast.LENGTH_SHORT).show();
            return;
        }

        displayMarksForStudent(roll);
    }

    private void displayAllMarks() {
        etSearchRoll.setText("");
        tableMarks.removeAllViews();
        createTableHeader();

        Cursor cursor = dbHelper.getMarksWithPercentage();
        boolean hasData = false;

        if (cursor != null && cursor.moveToFirst()) {
            hasData = true;
            String currentRoll = "";

            do {
                String roll = cursor.getString(cursor.getColumnIndexOrThrow("roll"));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String marks = cursor.getString(cursor.getColumnIndexOrThrow("marks_obtained"));
                double percentage = cursor.getDouble(cursor.getColumnIndexOrThrow("percentage"));

                // Add roll header if it's a new student
                if (!roll.equals(currentRoll)) {
                    currentRoll = roll;
                    addStudentHeaderRow(roll, studentName);
                }

                addMarksRow(roll, subject, marks, percentage);

            } while (cursor.moveToNext());

            cursor.close();
        }

        showData(hasData);

        if (!hasData) {
            Toast.makeText(this, "No marks data found", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayMarksForStudent(String roll) {
        tableMarks.removeAllViews();
        createTableHeader();

        // Verify student exists
        if (!dbHelper.checkStudentExists(roll)) {
            Toast.makeText(this, "Student with roll " + roll + " not found", Toast.LENGTH_SHORT).show();
            showData(false);
            return;
        }

        Cursor cursor = dbHelper.getMarksByRoll(roll);
        boolean hasData = false;

        if (cursor != null && cursor.moveToFirst()) {
            hasData = true;

            // Get student name for header
            String studentName = getStudentName(roll);
            addStudentHeaderRow(roll, studentName);

            int totalObtained = 0;
            int totalTotal = 0;
            int subjectCount = 0;

            do {
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String marksStr = cursor.getString(cursor.getColumnIndexOrThrow("marks_obtained"));

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

                addDetailedMarksRow(subject, obtained, total, percentage);

                totalObtained += obtained;
                totalTotal += total;
                subjectCount++;

            } while (cursor.moveToNext());

            cursor.close();

            // Add total row
            if (subjectCount > 0) {
                double overallPercentage = (totalTotal > 0) ? (totalObtained * 100.0 / totalTotal) : 0;
                addTotalRow(totalObtained, totalTotal, overallPercentage, subjectCount);
            }
        }

        showData(hasData);

        if (!hasData) {
            Toast.makeText(this, "No marks found for roll number: " + roll, Toast.LENGTH_SHORT).show();
        }
    }

    private String getStudentName(String roll) {
        Cursor cursor = dbHelper.getAllStudents();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String currentRoll = cursor.getString(cursor.getColumnIndexOrThrow("roll"));
                if (currentRoll.equals(roll)) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    cursor.close();
                    return name;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return "Unknown";
    }

    private void createTableHeader() {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        addHeaderCell(headerRow, "Roll No");
        addHeaderCell(headerRow, "Student Name");
        addHeaderCell(headerRow, "Subject");
        addHeaderCell(headerRow, "Marks");
        addHeaderCell(headerRow, "Percentage");

        tableMarks.addView(headerRow);
    }

    private void addStudentHeaderRow(String roll, String studentName) {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));

        TextView tvHeader = new TextView(this);
        tvHeader.setText("Student: " + roll + " - " + studentName);
        tvHeader.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        tvHeader.setTextSize(16);
        tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        tvHeader.setPadding(16, 12, 16, 12);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.span = 5; // Span across all columns
        tvHeader.setLayoutParams(params);

        headerRow.addView(tvHeader);
        tableMarks.addView(headerRow);
    }

    private void addMarksRow(String roll, String subject, String marks, double percentage) {
        TableRow row = new TableRow(this);

        addNormalCell(row, roll);
        addNormalCell(row, getStudentName(roll));
        addNormalCell(row, subject);
        addNormalCell(row, marks);
        addNormalCell(row, String.format("%.1f%%", percentage));

        // Color code based on percentage
        setRowColorBasedOnPercentage(row, percentage);

        tableMarks.addView(row);
    }

    private void addDetailedMarksRow(String subject, int obtained, int total, double percentage) {
        TableRow row = new TableRow(this);

        addNormalCell(row, subject);
        addNormalCell(row, obtained + "/" + total);
        addNormalCell(row, String.format("%.1f%%", percentage));

        // Color code based on percentage
        setRowColorBasedOnPercentage(row, percentage);

        tableMarks.addView(row);
    }

    private void addTotalRow(int totalObtained, int totalTotal, double overallPercentage, int subjectCount) {
        TableRow totalRow = new TableRow(this);
        totalRow.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_purple));

        addHeaderCell(totalRow, "TOTAL");
        addHeaderCell(totalRow, totalObtained + "/" + totalTotal);
        addHeaderCell(totalRow, String.format("%.1f%%", overallPercentage));
        addHeaderCell(totalRow, "Subjects: " + subjectCount);
        addHeaderCell(totalRow, "");

        tableMarks.addView(totalRow);
    }

    private void setRowColorBasedOnPercentage(TableRow row, double percentage) {
        if (percentage >= 75) {
            row.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
        } else if (percentage >= 50) {
            row.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        } else if (percentage > 0) {
            row.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextSize(14);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setPadding(12, 12, 12, 12);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(textView);
    }

    private void addNormalCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(12);
        textView.setPadding(12, 8, 12, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(textView);
    }

    private void showData(boolean hasData) {
        if (hasData) {
            tableMarks.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        } else {
            tableMarks.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }
}