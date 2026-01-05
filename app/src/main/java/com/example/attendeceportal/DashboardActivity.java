package com.example.attendeceportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    EditText studentName, lectureNo;
    Button markBtn, viewReportBtn;
    DBHelper db;
    int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        studentName = findViewById(R.id.studentName);
        lectureNo = findViewById(R.id.lectureNo);
        markBtn = findViewById(R.id.markBtn);
        viewReportBtn = findViewById(R.id.viewReportBtn);
        db = new DBHelper(this);

        teacherId = getIntent().getIntExtra("TID", -1);

        // --- Mark Attendance ---
        markBtn.setOnClickListener(v -> {
            String name = studentName.getText().toString();
            String lectureText = lectureNo.getText().toString();
            if(name.isEmpty() || lectureText.isEmpty()){
                Toast.makeText(this, "Enter Student Name and Lecture No", Toast.LENGTH_SHORT).show();
                return;
            }

            int lecture = Integer.parseInt(lectureText);

            db.markAttendance(teacherId, name, lecture, "Present"); // you can add dropdown for Absent later
            Toast.makeText(this, "Attendance Marked", Toast.LENGTH_SHORT).show();
            studentName.setText("");
            lectureNo.setText("");
        });

        // --- View Report ---
        viewReportBtn.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, ReportActivity.class);
            i.putExtra("TID", teacherId);
            startActivity(i);
        });
    }
}

