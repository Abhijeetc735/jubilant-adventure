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

    // Delete student by roll
    public boolean deleteStudent(String roll) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_STUDENT, COLUMN_ROLL + "=?", new String[]{roll});
        db.close();
        return rows > 0;
    }

    // Delete marks by roll
    public boolean deleteMarks(String roll) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MARKS, MARK_ROLL + "=?", new String[]{roll});
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

    // Update marks
    public boolean updateMarks(String roll, String subject, String newMarks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MARK_MARKS, newMarks);
        int rows = db.update(TABLE_MARKS, cv, MARK_ROLL + "=? AND " + MARK_SUBJECT + "=?", new String[]{roll, subject});
        db.close();
        return rows > 0;
    }
}