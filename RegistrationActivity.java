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

public class RegistrationActivity extends AppCompatActivity {

    private Button bt_signUp, bt_login;
    private EditText etEmail, etPassword, etPasswordCon;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordCon = findViewById(R.id.et_passwordCon);
        bt_login = findViewById(R.id.bt_Login);
        bt_signUp = findViewById(R.id.bt_signUp);

        // Sign-Up Button Click Listener
        bt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String passwordCon = etPasswordCon.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordCon)) {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register User with Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to the login page or home activity
                                Intent intent = new Intent(RegistrationActivity.this, NameSubActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Login Button Click Listener
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login Activity
                Toast.makeText(RegistrationActivity.this, "Redirecting to Login...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistrationActivity.this, Login_activity.class);
                startActivity(intent);
            }
        });
    }
}