package com.example.attendeceportal;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    TextView reportTv;
    DBHelper db;
    int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportTv = findViewById(R.id.reportTv);
        db = new DBHelper(this);
        teacherId = getIntent().getIntExtra("TID", -1);

        StringBuilder sb = new StringBuilder();
        Cursor c = db.getReport(teacherId);
        while(c.moveToNext()){
            String name = c.getString(0);
            int present = c.getInt(1);
            int total = c.getInt(2);
            sb.append(name).append(" → Present: ").append(present)
                    .append(" / Total: ").append(total).append("\n");
        }
        reportTv.setText(sb.toString());
    }
}
