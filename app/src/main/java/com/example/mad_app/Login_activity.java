package com.example.mad_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login_activity extends AppCompatActivity {

    private Button bt_login, bt_back;
    private EditText et_email, et_password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        bt_login = findViewById(R.id.bt_Login);
        bt_back = findViewById(R.id.bt_Back);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        // Login Button Click Listener
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login_activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Login User
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login_activity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to Home Activity
                                Intent intent = new Intent(Login_activity.this, HomeActivity.class);
                                startActivity(intent);
                                finish(); // Finish login activity
                            } else {
                                Toast.makeText(Login_activity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Back Button Click Listener
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the RegistrationActivity
                Intent intent = new Intent(Login_activity.this, RegistrationActivity.class);
                startActivity(intent);
                finish(); // Finish login activity
            }
        });
    }
}
