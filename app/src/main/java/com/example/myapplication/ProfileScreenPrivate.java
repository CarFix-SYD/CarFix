package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileScreenPrivate extends AppCompatActivity implements View.OnClickListener {

    public Button search,editProfile;
    public TextView userNanme;
    public FirebaseAuth mAuth;
    public DatabaseReference dRef;
    public String Path;
    public FirebaseUser currentUser;
    public String RegisterdID;


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

        search = (Button) findViewById(R.id.Search);
        search.setOnClickListener(this);

        editProfile = (Button) findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);





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
}