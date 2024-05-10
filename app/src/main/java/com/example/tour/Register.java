package com.example.tour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    private EditText firstname, lastname, email, password, confirm_password;
    private DbHelper dbhelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firstname = findViewById(R.id.txtfname);
        lastname = findViewById(R.id.txtlname);
        email = findViewById(R.id.txtemail);
        password = findViewById(R.id.txtpassword);
        confirm_password = findViewById(R.id.txtconfirmpassword);
        Button register = findViewById(R.id.btnregisternew);

        dbhelper = new DbHelper(Register.this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Fname = firstname.getText().toString();
                String Lname = lastname.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String ConfirmPassword = confirm_password.getText().toString();

                if (Fname.isEmpty() && Lname.isEmpty() && Email.isEmpty() && Password.isEmpty() && ConfirmPassword.isEmpty()) {
                    Toast.makeText(Register.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbhelper.registerUser(Fname, Lname, Email, Password, ConfirmPassword);
                AlertDialog dialog = getAlertDialog();


                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                TextView messageTextView = dialog.findViewById(android.R.id.message);
                if (messageTextView != null) {
                    messageTextView.setTextColor(getResources().getColor(android.R.color.black));
                }

                Button okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (okButton != null) {
                    okButton.setTextColor(getResources().getColor(android.R.color.white));

                    okButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                }
                firstname.setText("");
                lastname.setText("");
                email.setText("");
                password.setText("");
                confirm_password.setText("");
            }

        });

        TextView btnLogin = findViewById(R.id.btnloginagain);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @NonNull
    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setMessage("Data has been added. Please log in.");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
}