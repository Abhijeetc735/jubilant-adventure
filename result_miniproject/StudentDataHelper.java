package com.example.result_miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 2; // incremented since we add login fields

    // Student table
    private static final String TABLE_STUDENTS = "students";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";   // for login
    private static final String COL_PASSWORD = "password";   // for login
    private static final String COL_NAME = "name";
    private static final String COL_ROLL = "roll";
    private static final String COL_CLASS = "class";

    // Mapping table (teacher ↔ student)
    private static final String TABLE_TEACHER_STUDENTS = "teacher_students";
    private static final String COL_TEACHER_ID = "teacher_id";
    private static final String COL_STUDENT_ID = "student_id";

    public StudentDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ✅ Student table (with login info)
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_ROLL + " TEXT, "
                + COL_CLASS + " TEXT)";
        db.execSQL(CREATE_STUDENT_TABLE);

        // ✅ Mapping table (teacher ↔ student)
        String CREATE_MAP_TABLE = "CREATE TABLE " + TABLE_TEACHER_STUDENTS + "("
                + COL_TEACHER_ID + " INTEGER, "
                + COL_STUDENT_ID + " INTEGER)";
        db.execSQL(CREATE_MAP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHER_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // ✅ Register / insert a new student (used in registration)
    public boolean insertStudent(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return result != -1;
    }

    // ✅ Check if student exists by username
    public boolean isStudentExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ✅ Check login credentials
    public boolean checkStudentLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }

    // ✅ Add student (with name, roll, class) for a specific teacher
    public boolean addStudentForTeacher(int teacherId, String name, String roll, String studentClass) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert student details (if login data not required)
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_ROLL, roll);
        values.put(COL_CLASS, studentClass);

        long studentId = db.insert(TABLE_STUDENTS, null, values);
        if (studentId == -1) {
            db.close();
            return false;
        }

        // Create mapping (teacher ↔ student)
        ContentValues map = new ContentValues();
        map.put(COL_TEACHER_ID, teacherId);
        map.put(COL_STUDENT_ID, studentId);

        long mapResult = db.insert(TABLE_TEACHER_STUDENTS, null, map);
        db.close();
        return mapResult != -1;
    }
}
