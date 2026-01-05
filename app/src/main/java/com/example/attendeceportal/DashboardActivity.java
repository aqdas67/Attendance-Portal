package com.example.attendeceportal;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    Spinner studentSpinner;
    EditText lectureNo;
    Button addStudentBtn, markBtn, viewReportBtn, logoutBtn;
    DBHelper db;
    int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        studentSpinner = findViewById(R.id.studentSpinner);
        lectureNo = findViewById(R.id.lectureNo);
        addStudentBtn = findViewById(R.id.addStudentBtn);
        markBtn = findViewById(R.id.markBtn);
        viewReportBtn = findViewById(R.id.viewReportBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        db = new DBHelper(this);

        teacherId = getIntent().getIntExtra("TID", -1);

        // Insert 15 default students if DB empty
        db.insertDefaultStudentsOnce();

        // Load students into spinner
        refreshStudentSpinner();

        // ---------- Add Student ----------
        addStudentBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Student Name");

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean added = db.addStudent(name);
                if (added) {
                    Toast.makeText(this, name + " added successfully", Toast.LENGTH_SHORT).show();
                    refreshStudentSpinner(); // update spinner immediately
                } else {
                    Toast.makeText(this, "Student already exists", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // ---------- Mark Attendance ----------
        markBtn.setOnClickListener(v -> {
            String name = studentSpinner.getSelectedItem().toString();
            String lectureText = lectureNo.getText().toString().trim();

            if (lectureText.isEmpty()) {
                Toast.makeText(this, "Enter Lecture Number", Toast.LENGTH_SHORT).show();
                return;
            }

            int lecture = Integer.parseInt(lectureText);

            // Mark attendance
            db.markAttendance(teacherId, name, lecture, "Present");

            Toast.makeText(this, "Attendance marked for " + name, Toast.LENGTH_SHORT).show();
            lectureNo.setText("");
        });

        // ---------- View Report ----------
        viewReportBtn.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, ReportActivity.class);
            i.putExtra("TID", teacherId);
            startActivity(i);
        });

        // ---------- Logout ----------
        logoutBtn.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    // ---------- Helper method: load all students into spinner ----------
    private void refreshStudentSpinner() {
        ArrayList<String> studentList = new ArrayList<>();
        Cursor c = db.getAllStudents();
        if (c.moveToFirst()) {
            do {
                studentList.add(c.getString(0)); // student name
            } while (c.moveToNext());
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, studentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(adapter);
    }
}
