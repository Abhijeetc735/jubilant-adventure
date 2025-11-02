package com.example.result_miniproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class AddMarksActivity extends AppCompatActivity {

    private Spinner spinnerStudents;
    private TableLayout tableSubjects;
    private Button btnCalculate, btnBack;
    private AddStudentDB dbHelper; // ✅ Use only AddStudentDB

    // Predefined subjects
    private String[] subjects = {
            "Mathematics",
            "Physics",
            "Chemistry",
            "English",
            "Computer Science"
    };

    // Store EditText references
    private HashMap<String, EditText> totalMarksMap = new HashMap<>();
    private HashMap<String, EditText> obtainedMarksMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        dbHelper = new AddStudentDB(this); // ✅ Only one database helper

        // Initialize views
        spinnerStudents = findViewById(R.id.spinnerStudents);
        tableSubjects = findViewById(R.id.tableSubjects);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnBack = findViewById(R.id.btnBack);

        setupStudentSpinner();
        createSubjectTable();
        setupButtonListeners();
    }

    private void setupStudentSpinner() {
        ArrayList<String> studentList = new ArrayList<>();
        studentList.add("Select Student"); // Default option

        Cursor cursor = dbHelper.getAllStudentsForSpinner();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String roll = cursor.getString(cursor.getColumnIndexOrThrow("roll"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                studentList.add(roll + " - " + name);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, studentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudents.setAdapter(adapter);
    }

    private void createSubjectTable() {
        // Create header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Subject Name Header
        TextView headerSubject = createHeaderTextView("Subject Name");
        TableRow.LayoutParams subjectParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        headerSubject.setLayoutParams(subjectParams);

        // Total Marks Header
        TextView headerTotal = createHeaderTextView("Total Marks");
        TableRow.LayoutParams totalParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
        headerTotal.setLayoutParams(totalParams);

        // Obtained Marks Header
        TextView headerObtained = createHeaderTextView("Obtained Marks");
        TableRow.LayoutParams obtainedParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
        headerObtained.setLayoutParams(obtainedParams);

        headerRow.addView(headerSubject);
        headerRow.addView(headerTotal);
        headerRow.addView(headerObtained);

        tableSubjects.addView(headerRow);

        // Create rows for each subject
        for (String subject : subjects) {
            TableRow subjectRow = new TableRow(this);
            subjectRow.setBackgroundColor(getResources().getColor(android.R.color.background_light));

            // Subject Name Column
            TextView tvSubject = createSubjectTextView(subject);
            TableRow.LayoutParams subjectNameParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            tvSubject.setLayoutParams(subjectNameParams);

            // Total Marks Column (EditText)
            EditText etTotal = createMarksEditText();
            etTotal.setHint("100");
            TableRow.LayoutParams totalMarksParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
            etTotal.setLayoutParams(totalMarksParams);

            // Obtained Marks Column (EditText)
            EditText etObtained = createMarksEditText();
            etObtained.setHint("0");
            TableRow.LayoutParams obtainedMarksParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f);
            etObtained.setLayoutParams(obtainedMarksParams);

            subjectRow.addView(tvSubject);
            subjectRow.addView(etTotal);
            subjectRow.addView(etObtained);

            tableSubjects.addView(subjectRow);

            // Store references for later access
            totalMarksMap.put(subject, etTotal);
            obtainedMarksMap.put(subject, etObtained);
        }
    }

    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setPadding(16, 12, 16, 12);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        return textView;
    }

    private TextView createSubjectTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setPadding(16, 12, 16, 12);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        return textView;
    }

    private EditText createMarksEditText() {
        EditText editText = new EditText(this);
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        editText.setPadding(12, 12, 12, 12);
        editText.setTextSize(14);
        editText.setBackground(getResources().getDrawable(android.R.drawable.editbox_background));
        return editText;
    }

    private void setupButtonListeners() {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllMarks();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveAllMarks() {
        String selectedStudent = spinnerStudents.getSelectedItem().toString();

        // Check if student is selected
        if (selectedStudent.equals("Select Student")) {
            Toast.makeText(this, "Please select a student", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract roll number from spinner selection (format: "roll - name")
        String roll = selectedStudent.split(" - ")[0];
        boolean hasValidData = false;
        boolean hasErrors = false;

        // Process each subject
        for (String subject : subjects) {
            EditText etTotal = totalMarksMap.get(subject);
            EditText etObtained = obtainedMarksMap.get(subject);

            String totalStr = etTotal.getText().toString().trim();
            String obtainedStr = etObtained.getText().toString().trim();

            // Skip if both fields are empty
            if (totalStr.isEmpty() && obtainedStr.isEmpty()) {
                continue;
            }

            // Check if both fields are filled
            if (totalStr.isEmpty() || obtainedStr.isEmpty()) {
                Toast.makeText(this, "Please fill both marks for " + subject, Toast.LENGTH_SHORT).show();
                hasErrors = true;
                break;
            }

            try {
                int totalMarks = Integer.parseInt(totalStr);
                int obtainedMarks = Integer.parseInt(obtainedStr);

                // Validate marks
                if (totalMarks <= 0) {
                    Toast.makeText(this, "Total marks must be greater than 0 for " + subject, Toast.LENGTH_SHORT).show();
                    hasErrors = true;
                    break;
                }

                if (obtainedMarks < 0 || obtainedMarks > totalMarks) {
                    Toast.makeText(this, "Obtained marks must be between 0 and " + totalMarks + " for " + subject, Toast.LENGTH_SHORT).show();
                    hasErrors = true;
                    break;
                }

                // Save to database using detailed marks method
                boolean success = dbHelper.insertDetailedMarks(roll, subject, totalMarks, obtainedMarks);

                if (!success) {
                    Toast.makeText(this, "Error saving marks for " + subject, Toast.LENGTH_SHORT).show();
                    hasErrors = true;
                    break;
                }

                hasValidData = true;

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers for " + subject, Toast.LENGTH_SHORT).show();
                hasErrors = true;
                break;
            }
        }

        if (hasErrors) {
            return;
        }

        if (!hasValidData) {
            Toast.makeText(this, "Please enter marks for at least one subject", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show success message
        Toast.makeText(this, "Marks added successfully!", Toast.LENGTH_SHORT).show();

        // Clear all fields after successful save
        clearAllFields();
    }

    private void clearAllFields() {
        for (String subject : subjects) {
            totalMarksMap.get(subject).setText("");
            obtainedMarksMap.get(subject).setText("");
        }
        spinnerStudents.setSelection(0);
    }
}