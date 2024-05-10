package com.example.tour;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);



        // Initialize database helper and copy prepopulated database if necessary
        DbHelper dbHelper = new DbHelper(this);

        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.btnlogin);

        // Set click listener for login button
        loginButton.setOnClickListener(v -> {
            // Retrieve email and password from EditText fields
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Check if email and password are not empty
            if (!email.isEmpty() && !password.isEmpty()) {
                // Check if login is successful
                if (dbHelper.loginUser(email, password)) {
                    // If login is successful, show a success message
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, mainscreen.class);
                    startActivity(intent);

                    // Finish the current activity to prevent going back to it after logging out
                    finish();
                    // Here you can navigate to another activity or perform any other action upon successful login
                } else {
                    // If login is not successful, show an error message
                    Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If email or password is empty, show an error message
                Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for register button
        TextView btnRegister = findViewById(R.id.btnregister);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        // Apply window insets to handle system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
