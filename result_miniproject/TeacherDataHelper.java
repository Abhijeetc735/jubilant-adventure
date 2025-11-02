package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TeacherDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TeacherDB.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_TEACHER = "teachers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public TeacherDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TEACHER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE);

        // ❌ REMOVED: No default teacher - teachers register themselves
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER);
        onCreate(db);
    }

    // ✅ Register new teacher
    public boolean registerTeacher(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_TEACHER, null, values);
        db.close();
        return result != -1;
    }

    // ✅ Check if teacher exists
    public boolean isTeacherExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHER + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // ✅ Check teacher login credentials
    public boolean checkTeacherLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHER + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});
        boolean valid = cursor.moveToFirst();
        cursor.close();
        db.close();
        return valid;
    }

    // ✅ Change teacher password
    public boolean changeTeacherPassword(String username, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First verify old password
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TEACHER + " WHERE " +
                        COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, oldPassword});

        boolean oldPasswordCorrect = cursor.moveToFirst();
        cursor.close();

        if (oldPasswordCorrect) {
            // Old password is correct, update to new password
            ContentValues values = new ContentValues();
            values.put(COLUMN_PASSWORD, newPassword);

            int rowsAffected = db.update(TABLE_TEACHER, values,
                    COLUMN_USERNAME + "=?", new String[]{username});

            db.close();
            return rowsAffected > 0;
        } else {
            db.close();
            return false; // Old password is incorrect
        }
    }

    // ✅ ADD THIS: Debug method to check current password
    public String getCurrentPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_TEACHER + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            String currentPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            cursor.close();
            db.close();
            return currentPassword;
        } else {
            cursor.close();
            db.close();
            return "User not found";
        }
    }
}