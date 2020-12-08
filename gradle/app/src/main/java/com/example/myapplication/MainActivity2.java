package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button profileBUtton = findViewById(R.id.profilebutton);
        Intent intent = getIntent();
        String uName = intent.getExtras().getString("uName");
        String pUser = intent.getExtras().getString("pUser");
        profileBUtton.setText(uName);
    }
}