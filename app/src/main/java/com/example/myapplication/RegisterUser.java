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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private Button registerUser;
    private EditText editTextFullName,editTextAge,editTextEmail,editTexPassword;
    private ProgressBar probar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button)findViewById(R.id.registerbutton);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.Email);
        editTexPassword = (EditText) findViewById(R.id.password);

        probar = (ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registerbutton:
                registerUser();
                break;

        }
        startActivity(new Intent(this,MainActivity.class));

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String fullname = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String password = editTexPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTexPassword.setError("Please enter E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter valid E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(fullname.isEmpty()){
            editTextFullName.setError("Please enter E-mail");
            editTextFullName.requestFocus();
            return;
        }



        //validation if rmail is empty or not valid for example

        probar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(fullname,age,password);

                            FirebaseDatabase.getInstance().getReference("Users").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this ,"User is registered successfully",Toast.LENGTH_LONG).show();
                                        probar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(RegisterUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                                        probar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                            probar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}