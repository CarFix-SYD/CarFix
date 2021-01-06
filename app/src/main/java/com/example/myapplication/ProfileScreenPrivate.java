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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.AlertDialog;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class profileScreenPrivate extends AppCompatActivity implements View.OnClickListener, changePasswordDialog.ExampleDialogListener,leaveReviewDialog.leaveReivewInterface {

    public Button editProfile;
    public TextView userNanme;
    public FirebaseAuth mAuth;
    public DatabaseReference dRef;
    public String Path;
    public FirebaseUser currentUser;
    public String RegisterdID;
    private AlertDialog alertDialog = null;
    public ImageButton settings, search;

    private ListView listViewAppointemnts;
    private appointmentAdapterPrivate adapter;

    public DatabaseReference businessRef;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        dRef = FirebaseDatabase.getInstance().getReference("PrivateUsers");
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

        populateListViewAppointemts();
    }




    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editProfile:
                Intent intent = new Intent(profileScreenPrivate.this, editPrivateUserProfile.class);
                intent.putExtra("Path",Path);
                startActivity(intent);
                break;
            case R.id.Search:
                Intent search = new Intent(profileScreenPrivate.this,searchScreen.class);
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
                                Intent intent = new Intent(profileScreenPrivate.this, profileScreenPrivate.class);
                                Toast.makeText(profileScreenPrivate.this, "Profile updated", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(profileScreenPrivate.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(profileScreenPrivate.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateListViewAppointemts() {
        listViewAppointemnts = (ListView) findViewById(R.id.listOfAppointments);
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
                        if(System.currentTimeMillis() < dateCheck.getTime())
                            list.add(app);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                adapter = new appointmentAdapterPrivate(profileScreenPrivate.this, list);
                listViewAppointemnts.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void openDialog() {
        changePasswordDialog exampleDialog = new changePasswordDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    //to save the review to the Business
    @Override
    public void saveReivew(String review, String businessID, String userID,String reviewRating) {
            businessRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+businessID);

            businessRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("reviews/" + userID).exists()) {
                        Toast.makeText(profileScreenPrivate.this, "You cant leave review again", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(profileScreenPrivate.this, "Your review have been saved", Toast.LENGTH_LONG).show();
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