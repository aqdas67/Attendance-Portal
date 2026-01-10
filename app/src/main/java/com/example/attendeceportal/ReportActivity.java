package com.example.attendeceportal;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    RecyclerView reportRecycler;
    DBHelper db;
    int teacherId;
    ArrayList<StudentReport> reportList;
    ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize RecyclerView
        reportRecycler = findViewById(R.id.reportRecycler);
        reportRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database helper
        db = new DBHelper(this);

        // Get teacher ID passed from Dashboard
        teacherId = getIntent().getIntExtra("TID", -1);

        // Initialize report list and adapter
        reportList = new ArrayList<>();
        adapter = new ReportAdapter(reportList);
        reportRecycler.setAdapter(adapter);

        // Load report data
        loadReport();
    }

    private void loadReport() {
        reportList.clear();

        Cursor c = db.getReport(teacherId);  // Your DBHelper should return a Cursor
        if (c != null) {
            while (c.moveToNext()) {
                String name = c.getString(0);     // Student name
                int present = c.getInt(1);       // Present count
                int total = c.getInt(2);         // Total lectures

                reportList.add(new StudentReport(name, present, total));
            }
            c.close();
        }

        adapter.notifyDataSetChanged();
    }
}
