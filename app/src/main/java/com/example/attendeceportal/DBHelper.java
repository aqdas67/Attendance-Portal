package com.example.attendeceportal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "attendance.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE teachers (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE attendance (teacher_id INTEGER, student_id INTEGER, lecture_no INTEGER, status TEXT, UNIQUE(teacher_id, student_id, lecture_no))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS teachers");
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS attendance");
        onCreate(db);
    }

    // -------- Teacher Login --------
    public int teacherLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM teachers WHERE username=? AND password=?", new String[]{username, password});
        if(c.moveToFirst()) return c.getInt(0);
        return -1;
    }

    // -------- Add Teacher (Run once) --------
    public void addTeacher(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        db.insert("teachers", null, cv);
    }

    // -------- Add teacher for registration (NEW) --------
    public boolean addTeacherNew(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insertWithOnConflict("teachers", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1; // true if inserted, false if username exists
    }

    // -------- Get Student ID (Insert if not exists) --------
    private int getStudentId(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM students WHERE name=?", new String[]{name});
        if(c.moveToFirst()) return c.getInt(0);
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return (int) db.insert("students", null, cv);
    }

    // -------- Mark Attendance --------
    public void markAttendance(int teacherId, String studentName, int lectureNo, String status) {
        int studentId = getStudentId(studentName);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("teacher_id", teacherId);
        cv.put("student_id", studentId);
        cv.put("lecture_no", lectureNo);
        cv.put("status", status);
        db.insertWithOnConflict("attendance", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // -------- Attendance Report --------
    // -------- Attendance Report --------
    public Cursor getReport(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Count how many lectures were marked for each student
        return db.rawQuery(
                "SELECT s.name, SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) AS present, COUNT(*) AS total " +
                        "FROM attendance a JOIN students s ON s.id=a.student_id " +
                        "WHERE a.teacher_id=? " +
                        "GROUP BY s.name",
                new String[]{String.valueOf(teacherId)});
    }

}
