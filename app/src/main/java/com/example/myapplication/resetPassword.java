package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword extends AppCompatActivity {
    private EditText EditEmail;
    private Button resetPasswordButton;

    FirebaseAuth auth;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        EditEmail = (EditText) findViewById(R.id.resetEmail);
        resetPasswordButton = (Button) findViewById(R.id.ResetButton);
        progressbar = (ProgressBar) findViewById(R.id.ResetProgressBar);
        progressbar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpassword();
            }
        });

    }

    private void resetpassword() {
        String email = EditEmail.getText().toString().trim();


        if (email.isEmpty()){
            EditEmail.setError("Email is required");
            EditEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(resetPassword.this,"Invalid Email",Toast.LENGTH_LONG).show();
            EditEmail.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(resetPassword.this,"Check your Email",Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                    startActivity(new Intent(resetPassword.this, LoginActivity.class));

                }else{
                    Toast.makeText(resetPassword.this,"Opps! Try again",Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }
}