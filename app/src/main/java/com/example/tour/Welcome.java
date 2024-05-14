package com.example.tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Hide the status bar and the navigation bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the action bar (if your activity extends AppCompatActivity)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set content view after hiding the action bar
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView skip = findViewById(R.id.txtskip);
        skip.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome.this, Register.class);
            startActivity(intent);
        });


        Button Welcome = findViewById(R.id.btnwelcome);
        Welcome.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome.this, intro_1.class);
            startActivity(intent);
        });

        TextView signIn = findViewById(R.id.txtsignin);
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome.this, MainActivity.class);
            startActivity(intent);
        });
    }
}