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

        // Insert default 15 students
        insertDefaultStudents(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS teachers");
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS attendance");
        onCreate(db);
    }

    // --- Insert default students (internal) ---
    private void insertDefaultStudents(SQLiteDatabase db) {
        String[] students = {"Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace",
                "Hannah", "Ivy", "Jack", "Kiran", "Liam", "Mia", "Nora", "Oscar"};

        for (String s : students) {
            ContentValues cv = new ContentValues();
            cv.put("name", s);
            db.insertWithOnConflict("students", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    // --- Insert default students once (callable from activity) ---
    public void insertDefaultStudentsOnce() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM students", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();

        if (count == 0) { // only insert if table is empty
            insertDefaultStudents(db);
        }
    }

    // --- Add teacher (registration) ---
    public boolean addTeacherNew(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long result = db.insertWithOnConflict("teachers", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    // --- Teacher login ---
    public int teacherLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM teachers WHERE username=? AND password=?", new String[]{username, password});
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        return id;
    }

    // --- Add a new student dynamically ---
    public boolean addStudent(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        long result = db.insertWithOnConflict("students", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1; // true if added, false if already exists
    }

    // --- Get student id ---
    private int getStudentId(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM students WHERE name=?", new String[]{name});
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();

        if (id == -1) { // insert if not exists
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            id = (int) db.insert("students", null, cv);
        }
        return id;
    }

    // --- Mark attendance ---
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

    // --- Get attendance report per teacher ---
    public Cursor getReport(int teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT s.name, SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) AS present, COUNT(*) AS total " +
                        "FROM attendance a JOIN students s ON s.id=a.student_id " +
                        "WHERE a.teacher_id=? " +
                        "GROUP BY s.name",
                new String[]{String.valueOf(teacherId)});
    }

    // --- Get all students for spinner ---
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT name FROM students ORDER BY name ASC", null);
    }

}

