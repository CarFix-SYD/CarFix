package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class PrivateUserHistory extends AppCompatActivity implements leaveReviewDialog.leaveReivewInterface {
    private ListView listViewAppointmentsHistory;
    private DatabaseReference dRef;
    private String userID;
    private FirebaseUser currentUser;
    private appointmentAdapterPrivate adapter;
    private DatabaseReference businessRef;
    private Button BackToProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_user_history);
        dRef = FirebaseDatabase.getInstance().getReference("PrivateUsers");


        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView userName = (TextView) findViewById(R.id.userNameHistory);
        userName.setText(currentUser.getEmail().split("@")[0] + "\nTreatments History");

        BackToProfile = (Button) findViewById(R.id.BackToPrivateProfile);
        BackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateUserHistory.this,ProfileScreenPrivate.class);
                startActivity(intent);
            }
        });

        populateListViewAppointmentsForHistory();

    }

    private void populateListViewAppointmentsForHistory() {
        listViewAppointmentsHistory = (ListView) findViewById(R.id.historyListView);
        ArrayList<Appointment> list = new ArrayList<Appointment>();
        dRef.child(currentUser.getUid()).child("BookedTreatment").addValueEventListener(new ValueEventListener() {
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

                adapter = new appointmentAdapterPrivate(PrivateUserHistory.this, list);
                listViewAppointmentsHistory.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    @Override
    public void saveReivew(String review, String businessID, String userID, String reviewRating) {
        businessRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+businessID);

        businessRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("reviews/" + userID).exists()) {
                    Toast.makeText(PrivateUserHistory.this, "You cant leave review again", Toast.LENGTH_LONG).show();
                } else {
                    if (snapshot.child("reviews").exists()) {
                        businessRef.child("reviews/" + userID).setValue(review +", the rating is:"+ reviewRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                long numberOfReviews = snapshot.child("reviews").getChildrenCount();
                                String avgrating = snapshot.child("AVGrating").getValue().toString().trim();
                                businessRef.child("AVGrating").setValue(String.valueOf((Float.parseFloat(avgrating) + Float.parseFloat(reviewRating)) / numberOfReviews));
                            }
                        });

                    } else {
                        businessRef.child("reviews/" + userID).setValue(review +", the rating is:"+ reviewRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                businessRef.child("AVGrating").setValue(reviewRating);
                                Toast.makeText(PrivateUserHistory.this, "Your review have been saved", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Toast.makeText(this,"Hello",Toast.LENGTH_LONG).show();
    }
}