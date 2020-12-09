package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.AlertDialog;


import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileScreenPrivate extends AppCompatActivity implements View.OnClickListener, PasswordDialog.ExampleDialogListener {

    public Button editProfile;
    public TextView userNanme;
    public FirebaseAuth mAuth;
    public DatabaseReference dRef;
    public String Path;
    public FirebaseUser currentUser;
    public String RegisterdID;
    private AlertDialog alertDialog = null;
    public ImageButton settings, search;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RegisterdID = currentUser.getUid();
        //the path to the user in firebase
        Path = FirebaseDatabase.getInstance().getReference("/PrivateUsers/" + RegisterdID).toString();


        userNanme = (TextView) findViewById(R.id.HelloUser);
        userNanme.setText("Hello " + currentUser.getEmail().split("@")[0]);

        search = (ImageButton) findViewById(R.id.Search);
        search.setOnClickListener(this);

        editProfile = (Button) findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);

        settings = (ImageButton) findViewById(R.id.SettingButtonP);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }
    public void openDialog() {
        PasswordDialog exampleDialog = new PasswordDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editProfile:
                Intent intent = new Intent(ProfileScreenPrivate.this,EditPrivateUserProfile.class);
                intent.putExtra("Path",Path);
                startActivity(intent);
                break;
            case R.id.Search:
                Intent search = new Intent(ProfileScreenPrivate.this,searchScreen.class);
                search.putExtra("profileName",currentUser.getEmail().split("@")[0]);
                startActivity(search);
                break;
        }
    }

    @Override
    public void applyTexts(String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("PrivateUsers");
        userRef.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String identifier = currentuid;

                    Map<String, Object> values = new HashMap<>();
                    values.put("Password", password);
                    if(!values.isEmpty()) {
                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        userRef.child(identifier).updateChildren(values, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Intent intent = new Intent(ProfileScreenPrivate.this, ProfileScreenPrivate.class);
                                Toast.makeText(ProfileScreenPrivate.this, "Profile updated", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(ProfileScreenPrivate.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ProfileScreenPrivate.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}