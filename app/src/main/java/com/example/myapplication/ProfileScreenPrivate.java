package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

public class ProfileScreenPrivate extends AppCompatActivity implements View.OnClickListener, changePasswordDialog.ExampleDialogListener,leaveReviewDialog.leaveReivewInterface {

    public Button editProfile;
    public TextView userNanme;
    public FirebaseAuth mAuth;
    public DatabaseReference dRef;
    public String Path;
    public FirebaseUser currentUser;
    public String RegisterdID;
    private AlertDialog alertDialog = null;
    public ImageButton settings, search,logout;

    private ListView listViewAppointments;
    private appointmentAdapterPrivate adapter;

    public DatabaseReference businessRef;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        //FireBase variables
        dRef = FirebaseDatabase.getInstance().getReference("PrivateUsers");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RegisterdID = currentUser.getUid();

        //the path to the user in firebase
        Path = FirebaseDatabase.getInstance().getReference("/PrivateUsers/" + RegisterdID).toString();

        // Text view with Hello + user name
        userNanme = (TextView) findViewById(R.id.HelloUser);
        userNanme.setText("Hello " + currentUser.getEmail().split("@")[0]);

        //search button
        search = (ImageButton) findViewById(R.id.Search);
        search.setOnClickListener(this);

        editProfile = (Button) findViewById(R.id.editProfile);
        editProfile.setOnClickListener(this);

        //button for changing the password
        settings = (ImageButton) findViewById(R.id.SettingButtonP);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        //log out button
        logout = (ImageButton) findViewById(R.id.privatelogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        populateListViewAppointments();//function for showing the list view in the private profile
    }


    /**
     * log out() - for logging out from the app, using dialog to make shore.
     */
    private void logout() {
        AlertDialog questionLogOut = new AlertDialog.Builder(ProfileScreenPrivate.this).
                setTitle("Do you want to log out?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileScreenPrivate.this, LoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeContextMenu();
            }
        }).create();
        questionLogOut.show();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editProfile:
                Intent intent = new Intent(ProfileScreenPrivate.this, EditPrivateUserProfile.class);
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

    /**
     * Dialog for changing the password for user
     * @param password - the user password
     */
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

    /**
     * This function use for display the Appointments of every user
     * Showing only the future Appointments, and make two buttons:
     * *review: leave review for the treatment
     * *cancel: to cancel the appointment
     */
    private void populateListViewAppointments() {
        listViewAppointments = (ListView) findViewById(R.id.listOfAppointments);
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

                adapter = new appointmentAdapterPrivate(ProfileScreenPrivate.this, list);
                listViewAppointments.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /**
     * Dialog for changing the Password
     */
    public void openDialog() {
        changePasswordDialog exampleDialog = new changePasswordDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    /**
     * Function to save review from private user to business.
     * @param review - String with the review
     * @param businessID - the ID of the business user in the DataBase
     * @param userID - the ID of the private user in the DataBase
     * @param reviewRating - the rating for the business from the private user
     */
    @Override
    public void saveReivew(String review, String businessID, String userID,String reviewRating) {
            businessRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+businessID);

            businessRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("reviews/" + userID).exists()) {
                        Toast.makeText(ProfileScreenPrivate.this, "You cant leave review again", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(ProfileScreenPrivate.this, "Your review have been saved", Toast.LENGTH_LONG).show();
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