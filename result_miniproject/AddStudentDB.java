package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AddStudentDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_STUDENT = "students";
    private static final String TABLE_MARKS = "marks";

    // Student table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ROLL = "roll";
    private static final String COLUMN_CLASS = "class";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Marks table columns
    private static final String MARK_ID = "mark_id";
    private static final String MARK_ROLL = "roll";
    private static final String MARK_SUBJECT = "subject";
    private static final String MARK_MARKS = "marks_obtained";

    public AddStudentDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create students table
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_ROLL + " TEXT UNIQUE, "
                + COLUMN_CLASS + " TEXT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_STUDENT_TABLE);

        // Create marks table
        String CREATE_MARKS_TABLE = "CREATE TABLE " + TABLE_MARKS + " ("
                + MARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MARK_ROLL + " TEXT, "
                + MARK_SUBJECT + " TEXT, "
                + MARK_MARKS + " TEXT, "
                + "FOREIGN KEY (" + MARK_ROLL + ") REFERENCES " + TABLE_STUDENT + "(" + COLUMN_ROLL + "))";
        db.execSQL(CREATE_MARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKS);
        onCreate(db);
    }

    // ==================== STUDENT METHODS ====================

    // Insert new student
    public boolean insertStudent(String name, String roll, String studentClass, String username, String password) {
        if (checkStudentExists(roll) || checkUsernameExists(username))
            return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_ROLL, roll);
        cv.put(COLUMN_CLASS, studentClass);
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_STUDENT, null, cv);
        db.close();
        return result != -1;
    }

    // Check if student exists by roll number
    public boolean checkStudentExists(String roll) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_STUDENT + " WHERE " + COLUMN_ROLL + "=?",
                new String[]{roll});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Check if username already exists
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_STUDENT + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Verify student login credentials
    public boolean checkStudentLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENT + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password});
        boolean valid = cursor.moveToFirst();
        cursor.close();
        db.close();
        return valid;
    }

    // Get all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);
    }

    // Get all students for spinner
    public Cursor getAllStudentsForSpinner() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_ROLL + ", " + COLUMN_NAME + " FROM " + TABLE_STUDENT, null);
    }

    // Delete student by roll
    public boolean deleteStudent(String roll) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_STUDENT, COLUMN_ROLL + "=?", new String[]{roll});
        db.close();
        return rows > 0;
    }

    // Update student details
    public boolean updateStudent(String roll, String newName, String newClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, newName);
        cv.put(COLUMN_CLASS, newClass);
        int rows = db.update(TABLE_STUDENT, cv, COLUMN_ROLL + "=?", new String[]{roll});
        db.close();
        return rows > 0;
    }

    // ==================== MARKS METHODS ====================

    // Insert marks for student
    public boolean insertMarks(String roll, String subject, String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MARK_ROLL, roll);
        cv.put(MARK_SUBJECT, subject);
        cv.put(MARK_MARKS, marks);

        long result = db.insert(TABLE_MARKS, null, cv);
        db.close();
        return result != -1;
    }

    // Check if marks exist for student and subject
    public boolean checkMarksExist(String roll, String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MARKS + " WHERE " +
                MARK_ROLL + "=? AND " + MARK_SUBJECT + "=?", new String[]{roll, subject});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Update marks
    public boolean updateMarks(String roll, String subject, String newMarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MARK_MARKS, newMarks);
        int rows = db.update(TABLE_MARKS, cv, MARK_ROLL + "=? AND " + MARK_SUBJECT + "=?", new String[]{roll, subject});
        db.close();
        return rows > 0;
    }

    // Get marks by roll number
    public Cursor getMarksByRoll(String roll) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MARKS + " WHERE " + MARK_ROLL + "=?", new String[]{roll});
    }

    // Get all marks
    public Cursor getAllMarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MARKS, null);
    }

    // Get all marks ordered by roll and subject
    public Cursor getAllMarksOrdered() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MARKS + " ORDER BY " + MARK_ROLL + ", " + MARK_SUBJECT, null);
    }

    // Get student marks summary with student names
    public Cursor getMarksSummary() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "m." + MARK_ROLL + ", " +
                "s." + COLUMN_NAME + " as student_name, " +
                "m." + MARK_SUBJECT + ", " +
                "m." + MARK_MARKS +
                " FROM " + TABLE_MARKS + " m " +
                "LEFT JOIN " + TABLE_STUDENT + " s ON m." + MARK_ROLL + " = s." + COLUMN_ROLL + " " +
                "ORDER BY m." + MARK_ROLL + ", m." + MARK_SUBJECT;
        return db.rawQuery(query, null);
    }

    // Delete marks by roll
    public boolean deleteMarks(String roll) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MARKS, MARK_ROLL + "=?", new String[]{roll});
        db.close();
        return rows > 0;
    }

    // Insert marks with total and obtained marks (for table format)
    public boolean insertDetailedMarks(String roll, String subject, int totalMarks, int obtainedMarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MARK_ROLL, roll);
        cv.put(MARK_SUBJECT, subject);
        cv.put(MARK_MARKS, obtainedMarks + "/" + totalMarks); // Store as "obtained/total"

        long result = db.insert(TABLE_MARKS, null, cv);
        db.close();
        return result != -1;
    }

    // Get marks with percentage calculation
    public Cursor getMarksWithPercentage() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "m." + MARK_ROLL + ", " +
                "s." + COLUMN_NAME + " as student_name, " +
                "m." + MARK_SUBJECT + ", " +
                "m." + MARK_MARKS + ", " +
                "CASE " +
                "WHEN m." + MARK_MARKS + " LIKE '%/%' THEN " +
                "ROUND((CAST(SUBSTR(m." + MARK_MARKS + ", 1, INSTR(m." + MARK_MARKS + ", '/')-1) AS REAL) * 100.0 / " +
                "CAST(SUBSTR(m." + MARK_MARKS + ", INSTR(m." + MARK_MARKS + ", '/')+1) AS REAL)), 2) " +
                "ELSE 0 END as percentage " +
                "FROM " + TABLE_MARKS + " m " +
                "LEFT JOIN " + TABLE_STUDENT + " s ON m." + MARK_ROLL + " = s." + COLUMN_ROLL + " " +
                "ORDER BY m." + MARK_ROLL + ", m." + MARK_SUBJECT;
        return db.rawQuery(query, null);
    }
    // Add this method to your AddStudentDB.java in the STUDENT METHODS section
    public boolean updateStudentPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_STUDENT, cv,
                COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
        return rowsAffected > 0;
    }
    // ✅ Get student name by username
    public String getStudentName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_NAME + " FROM " + TABLE_STUDENT + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return ""; // Return empty string if not found
        }
    }
}