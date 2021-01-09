package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileScreenBusiness extends AppCompatActivity implements View.OnClickListener, changePasswordDialog.ExampleDialogListener,addDescriptionBsuinessDialog.addDescriptionInterface{
    public TextView helloUser;
    public Button editProfile;
    public ImageButton settings, logout;
    public FirebaseUser currentUser;
    public String RegisterdID;
    public String Path;
    private AlertDialog alertDialog = null;
    public ListView TreatmentList;
    private appointmentAdapterBusiness adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen_business);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RegisterdID = currentUser.getUid();
        Path = FirebaseDatabase.getInstance().getReference("/BusinessUsers/" + RegisterdID).toString();

        helloUser = (TextView) findViewById(R.id.HelloBusinessUser);
        helloUser.setText("Hello " +currentUser.getEmail().split("@")[0].trim());

        editProfile = (Button) findViewById(R.id.editBusinessUser);
        editProfile.setOnClickListener(this);


        settings = (ImageButton) findViewById(R.id.SettingButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        logout = (ImageButton) findViewById(R.id.Businesslogout);
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                                          logout();
                                      }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("BusinessUsers");
        populateListViewAppointments();



    }

    /**
     * log out() - for logging out from the app, using dialog to make shore.
     */
    private void logout() {
        AlertDialog questionLogOut = new AlertDialog.Builder(ProfileScreenBusiness.this).
                setTitle("Do you want to log out?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileScreenBusiness.this, LoginActivity.class));
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

    private void populateListViewAppointments() {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        TreatmentList = (ListView) findViewById(R.id.TreatmentListView);
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
                        if(System.currentTimeMillis() < dateCheck.getTime())
                            list.add(app);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                adapter = new appointmentAdapterBusiness(ProfileScreenBusiness.this, list);
                TreatmentList.setAdapter(adapter);

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


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editBusinessUser:
                Intent intent = new Intent(this, businessPageByUser.class);
                intent.putExtra("extraID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
                break;

        }
    }

    @Override
    public void applyTexts(String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("BusinessUsers");
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
                                Intent intent = new Intent(ProfileScreenBusiness.this, ProfileScreenBusiness.class);
                                Toast.makeText(ProfileScreenBusiness.this, "Profile updated", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(ProfileScreenBusiness.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ProfileScreenBusiness.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * this function use for saving the description in both users and app in the databse
     * @param Description - the treatment description
     * @param businessID - the business ID
     * @param userID - the user ID
     * @param date - the date of the treatment
     */
    @Override
    public void saveDescription(String Description, String businessID, String userID,String date) {
        DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+businessID + "/BookedTreatment/");
        DatabaseReference privateRef = FirebaseDatabase.getInstance().getReference("PrivateUsers/"+userID + "/BookedTreatment/");
        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("Appointments/");
        businessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String keyTreatment = "";
                if (snapshot.exists()) {
                    for (DataSnapshot treatmentKey : snapshot.getChildren()) {
                        if(treatmentKey.child("date").getValue().toString().equals(date) && treatmentKey.child("businessID").getValue().toString().equals(businessID) && treatmentKey.child("privateID").getValue().toString().equals(userID)){
                                keyTreatment = treatmentKey.getKey();
                                Log.i("Here is the app key",keyTreatment);
                                businessRef.child(keyTreatment).child("description").setValue(Description, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                                privateRef.child(keyTreatment).child("description").setValue(Description, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                                appRef.child(keyTreatment).child("description").setValue(Description, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                    }
                                });
                            }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}