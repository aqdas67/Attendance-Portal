package com.example.attendeceportal;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button loginBtn, registerBtn;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        db = new DBHelper(this);

        // --- LOGIN ---
        loginBtn.setOnClickListener(v -> {
            int teacherId = db.teacherLogin(
                    username.getText().toString(),
                    password.getText().toString()
            );

            if(teacherId != -1){
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
            String user = username.getText().toString();
            String pass = password.getText().toString();

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
