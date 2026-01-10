package com.example.attendeceportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;
    MaterialButton loginBtn, registerBtn;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- UI References ---
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        db = new DBHelper(this);

        // --- LOGIN ---
        loginBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            int teacherId = db.teacherLogin(user, pass);

            if (teacherId != -1) {
                // Login successful, go to dashboard
                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                i.putExtra("TID", teacherId);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        // --- REGISTER ---
        registerBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.addTeacherNew(user, pass);

            if(success){
                Toast.makeText(this, "Registration Successful! Login now.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
