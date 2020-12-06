package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileScreenBusiness extends AppCompatActivity implements View.OnClickListener{
    public TextView helloUser;
    public Button editProfile,search;
    public FirebaseUser currentUser;
    public String RegisterdID;
    public String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen_business);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RegisterdID = currentUser.getUid();
        Path = FirebaseDatabase.getInstance().getReference("/BusinessUsers/" + RegisterdID).toString();

        helloUser = (TextView) findViewById(R.id.HelloBusinessUser);
        helloUser.setText("Hello " + currentUser.getEmail().split("@")[0]);

        editProfile = (Button) findViewById(R.id.editBusinessUser);
        editProfile.setOnClickListener(this);

        search = (Button) findViewById(R.id.searchFromBusinessProfile);
        search.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editBusinessUser:
                Intent intent = new Intent(this,EditBusinessProfile.class);
                intent.putExtra("Path",Path);
                startActivity(intent);
                break;
            case R.id.searchFromBusinessProfile:
                startActivity(new Intent(this,searchScreen.class));
                break;
        }
    }
}