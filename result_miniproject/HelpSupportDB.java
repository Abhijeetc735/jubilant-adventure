package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelpSupportDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HelpSupportDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HELP_REQUESTS = "help_requests";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STUDENT_NAME = "student_name";
    private static final String COLUMN_CONTACT_NO = "contact_no";
    private static final String COLUMN_PROBLEM_DESCRIPTION = "problem_description";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public HelpSupportDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_HELP_REQUESTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_NAME + " TEXT, "
                + COLUMN_CONTACT_NO + " TEXT, "
                + COLUMN_PROBLEM_DESCRIPTION + " TEXT, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HELP_REQUESTS);
        onCreate(db);
    }

    // Insert new help request
    public boolean insertHelpRequest(String studentName, String contactNo, String problemDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, studentName);
        values.put(COLUMN_CONTACT_NO, contactNo);
        values.put(COLUMN_PROBLEM_DESCRIPTION, problemDescription);

        long result = db.insert(TABLE_HELP_REQUESTS, null, values);
        db.close();
        return result != -1;
    }

    // Get all help requests
    public Cursor getAllHelpRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HELP_REQUESTS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC", null);
    }
}