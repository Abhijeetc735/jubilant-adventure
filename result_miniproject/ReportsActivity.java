package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReportsActivity {
    public static class StudentDataHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "SchoolDB.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_STUDENT = "students";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_USERNAME = "username";
        private static final String COLUMN_PASSWORD = "password";
        private static final String COLUMN_EMAIL = "email";

        public StudentDataHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE_STUDENT = "CREATE TABLE IF NOT EXISTS " + TABLE_STUDENT + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USERNAME + " TEXT, "
                    + COLUMN_PASSWORD + " TEXT, "
                    + COLUMN_EMAIL + " TEXT)";
            db.execSQL(CREATE_TABLE_STUDENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
            onCreate(db);
        }

        // Register a new student
        public boolean registerStudent(String username, String password, String email) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USERNAME, username);
            cv.put(COLUMN_PASSWORD, password);
            cv.put(COLUMN_EMAIL, email);

            long result = db.insert(TABLE_STUDENT, null, cv);
            db.close();
            return result != -1;
        }

        // Check student login
        public boolean checkStudentLogin(String username, String password) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_STUDENT + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
            Cursor cursor = db.rawQuery(query, new String[]{username, password});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;
        }

        // Check if student already exists
        public boolean isStudentExists(String username) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_STUDENT + " WHERE " + COLUMN_USERNAME + "=?";
            Cursor cursor = db.rawQuery(query, new String[]{username});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;
        }
    }
}
