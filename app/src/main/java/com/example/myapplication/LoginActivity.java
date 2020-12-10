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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register,forgotPassword;
    private EditText editTextEmail,EditTextPassword;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView)findViewById(R.id.register);
        register.setOnClickListener(LoginActivity.this);
        forgotPassword =(TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.loginEmail);
        EditTextPassword = (EditText) findViewById(R.id.loginPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
                break;

            case R.id.loginButton:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this,resetPassword.class));
                break;
        }
    }


    private void userLogin() {

        //convert Text lines to Strings
        String userName = editTextEmail.getText().toString().trim();
        String password = EditTextPassword.getText().toString().trim();

        /**
        * checks for the user login validation of the email and password
        */
        if(userName.isEmpty()){
            editTextEmail.setError("Please enter E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userName).matches()){
            editTextEmail.setError("Please enter valid E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            EditTextPassword.setError("Please enter password");
            EditTextPassword.requestFocus();
            return;
        }

         //Progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String RegisterdID = currentUser.getUid();

                    /**
                     * the function is separated to two parts because of the Firebase structure.
                     * this function is for the business user login.
                     */
                    DatabaseReference jloginDatabaseBusiness = FirebaseDatabase.getInstance().getReference("/BusinessUsers/").child(RegisterdID);
                    jloginDatabaseBusiness.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userType = snapshot.child("type").getValue().toString();
                                if (userType.equals("BusinessUser")) {
                                    Intent intentBusiness = new Intent(LoginActivity.this, ProfileScreenBusiness.class);
                                    intentBusiness.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intentBusiness);
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    /**
                     * this Function is for private user login using reference and add value listener.
                     */
                    DatabaseReference jloginDatabasePrivate = FirebaseDatabase.getInstance().getReference("/PrivateUsers/").child(RegisterdID);
                    jloginDatabasePrivate.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userType = snapshot.child("type").getValue().toString();
                                if (userType.equals("PrivateUser")) {
                                    Intent intentPrivate = new Intent(LoginActivity.this, ProfileScreenPrivate.class);
                                    intentPrivate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intentPrivate);
                                    progressBar.setVisibility(View.GONE);
                                    return;

                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"Failed to login",Toast.LENGTH_LONG).show();;
                    progressBar.setVisibility(View.GONE);
                    return;
                }

            }
            });
    }
}