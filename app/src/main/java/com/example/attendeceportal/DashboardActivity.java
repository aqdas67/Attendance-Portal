package com.example.attendeceportal;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    TextInputEditText lectureNo;
    MaterialButton addStudentBtn, markBtn, viewReportBtn, logoutBtn;
    RecyclerView studentRecycler;

    DBHelper db;
    int teacherId;
    String selectedStudentName = null;

    ArrayList<String> studentsList;
    StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        lectureNo = findViewById(R.id.lectureNo);
        studentRecycler = findViewById(R.id.studentRecycler);
        addStudentBtn = findViewById(R.id.addStudentBtn);
        markBtn = findViewById(R.id.markBtn);
        viewReportBtn = findViewById(R.id.viewReportBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Initialize database and teacher ID
        db = new DBHelper(this);
        teacherId = getIntent().getIntExtra("TID", -1);
        db.insertDefaultStudentsOnce();

        // Setup RecyclerView
        studentsList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentsList, studentName -> {
            selectedStudentName = studentName;
            Toast.makeText(DashboardActivity.this, "Selected: " + studentName, Toast.LENGTH_SHORT).show();
        });

        studentRecycler.setLayoutManager(new LinearLayoutManager(this));
        studentRecycler.setAdapter(studentAdapter);

        loadStudents();

        // Add new student
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
                    loadStudents();
                } else {
                    Toast.makeText(this, "Student already exists", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Mark attendance
        markBtn.setOnClickListener(v -> {
            String lectureText = lectureNo.getText().toString().trim();
            if (lectureText.isEmpty()) {
                Toast.makeText(this, "Enter Lecture Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedStudentName == null) {
                Toast.makeText(this, "Select a student from the list", Toast.LENGTH_SHORT).show();
                return;
            }

            int lecture = Integer.parseInt(lectureText);
            db.markAttendance(teacherId, selectedStudentName, lecture, "Present");
            Toast.makeText(this, "Attendance marked for " + selectedStudentName, Toast.LENGTH_SHORT).show();
            lectureNo.setText("");
        });

        // View report
        viewReportBtn.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, ReportActivity.class);
            i.putExtra("TID", teacherId);
            startActivity(i);
        });

        // Logout
        logoutBtn.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    // Load students from DB
    private void loadStudents() {
        studentsList.clear();
        Cursor c = db.getAllStudents();
        if (c.moveToFirst()) {
            do {
                studentsList.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        studentAdapter.notifyDataSetChanged();
    }
}
