package com.example.result_miniproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewMarksActivity extends AppCompatActivity {

    EditText etSearchRoll;
    Button btnSearch, btnViewAll;
    LinearLayout marksContainer;
    TextView tvNoData;
    AddStudentDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);

        dbHelper = new AddStudentDB(this);

        // Initialize views
        etSearchRoll = findViewById(R.id.etSearchRoll);
        btnSearch = findViewById(R.id.btnSearch);
        btnViewAll = findViewById(R.id.btnViewAll);
        marksContainer = findViewById(R.id.marksContainer);
        tvNoData = findViewById(R.id.tvNoData);

        btnSearch.setOnClickListener(v -> searchMarksByRoll());

        btnViewAll.setOnClickListener(v -> viewAllMarks());

        viewAllMarks();
    }

    private void searchMarksByRoll() {
        String roll = etSearchRoll.getText().toString().trim();
        if (roll.isEmpty()) {
            Toast.makeText(this, "Please enter roll number", Toast.LENGTH_SHORT).show();
            return;
        }
        displayMarks(roll);
    }

    private void viewAllMarks() {
        displayMarks(null);
    }

    private void displayMarks(String specificRoll) {
        marksContainer.removeAllViews();

        Cursor cursor;
        if (specificRoll != null) {
            cursor = dbHelper.getMarksByRoll(specificRoll);
        } else {
            cursor = dbHelper.getAllMarks();
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String roll = cursor.getString(cursor.getColumnIndexOrThrow("roll"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String marks = cursor.getString(cursor.getColumnIndexOrThrow("marks_obtained"));

                // Create view for each marks entry
                View marksItem = createMarksItem(roll, subject, marks);
                marksContainer.addView(marksItem);

            } while (cursor.moveToNext());
            cursor.close();
            showData();
        } else {
            showNoData();
        }
    }

    private View createMarksItem(String roll, String subject, String marks) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(50, 30, 50, 30);
        itemLayout.setBackgroundResource(R.drawable.item_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        itemLayout.setLayoutParams(params);

        // Roll Number
        TextView tvRoll = new TextView(this);
        tvRoll.setText("Roll: " + roll);
        tvRoll.setTextSize(18);
        tvRoll.setTextColor(getResources().getColor(android.R.color.black));
        tvRoll.setTypeface(null, android.graphics.Typeface.BOLD);
        itemLayout.addView(tvRoll);

        // Subject
        TextView tvSubject = new TextView(this);
        tvSubject.setText("Subject: " + subject);
        tvSubject.setTextSize(16);
        tvSubject.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvSubject.setPadding(0, 8, 0, 0);
        itemLayout.addView(tvSubject);

        // Marks
        TextView tvMarks = new TextView(this);
        tvMarks.setText("Marks: " + marks);
        tvMarks.setTextSize(16);
        tvMarks.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvMarks.setPadding(0, 4, 0, 0);
        itemLayout.addView(tvMarks);

        return itemLayout;
    }

    private void showData() {
        marksContainer.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
    }

    private void showNoData() {
        marksContainer.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
    }
}