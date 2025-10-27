package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDataHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "SchoolDB.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_STUDENTS = "students";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public StudentDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // ✅ Insert student (for registration)
    public boolean insertStudent(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if user already exists
        if (isStudentExists(username)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return result != -1; // true if inserted successfully
    }

    // ✅ Check if student exists
    public boolean isStudentExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_USERNAME + " = ?",
                new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ✅ Check student login
    public boolean checkStudentLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENTS +
                        " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password});

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }
}
