package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BusinessUserHistory extends AppCompatActivity {

    public TextView helloUser;
    public ListView TreatmentList;
    private appointmentAdapterBusiness adapter;
    public FirebaseUser currentUser;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_user_history);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        helloUser = (TextView) findViewById(R.id.businessNameHistory);
        helloUser.setText(currentUser.getEmail().split("@")[0].trim() + "\n Treatments History");

        back = (Button) findViewById(R.id.backToBusiProfile);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessUserHistory.this,ProfileScreenBusiness.class);
                startActivity(intent);
            }
        });
        populateListViewAppointments();


    }

    private void populateListViewAppointments() {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        TreatmentList = (ListView) findViewById(R.id.TreatmentHistoryListView);
        ArrayList<Appointment> list = new ArrayList<Appointment>();
        dRef.child("BusinessUsers").child(currentUser.getUid()).child("BookedTreatment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String appDate = data.child("date").getValue().toString().trim();
                    String appBusinessID = data.child("businessID").getValue().toString().trim();
                    String appPrivateID = data.child("privateID").getValue().toString().trim();
                    Appointment app = new Appointment(appDate,appPrivateID,appBusinessID);

                    //check if the date is in the past if it is dont show it anymore
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
                    try {
                        Date dateCheck = sdf.parse(appDate);
                        if(System.currentTimeMillis() > dateCheck.getTime())
                            list.add(app);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                adapter = new appointmentAdapterBusiness(BusinessUserHistory.this, list);
                TreatmentList.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}