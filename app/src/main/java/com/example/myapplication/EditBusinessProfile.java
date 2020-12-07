package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditBusinessProfile extends AppCompatActivity implements View.OnClickListener {

    public EditText editEmail,editBusinessName,editBusinessAddress,editBusinessPhone;
    private Spinner kindOfBusiness,businessCity;
    public Button saveChanges;

    private Button carsInBusinessButton;
    private String[] listOfCars;
    private boolean[] checkCars;
    private ArrayList<Integer> carsBussines;
    private boolean WhichUser = true;
    private String finalListOfCarBusiness ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile);

        saveChanges = (Button) findViewById(R.id.editBusinessProfileSave);
        saveChanges.setOnClickListener(this);

        carsInBusinessButton = (Button) findViewById(R.id.editBusinessCarsToTreat);
        carsInBusinessButton.setOnClickListener(this);
        carsBussines = new ArrayList<Integer>();

        editBusinessName = (EditText) findViewById(R.id.editBusinessBusinessName);
        editEmail = (EditText) findViewById(R.id.editBusinessEmail);

        kindOfBusiness = (Spinner) findViewById(R.id.spinerBusinessKind);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.KindOfBusiness));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindOfBusiness.setAdapter(myAdapter1);

        //business address
        editBusinessAddress = (EditText) findViewById(R.id.editBusinessAddress);

        businessCity = (Spinner) findViewById(R.id.spinnerBusinessCity);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessCity.setAdapter(myAdapter2);

        //business Phone number
        editBusinessPhone = (EditText) findViewById(R.id.editBusinessPhone);
        /**
        * this part is for getting the data from firebase and show it
        */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("BusinessUsers");

        userRef.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String email = snapshot.child("Email").getValue(String.class);
                    String kindOfBusiness = snapshot.child("KindOfBusiness").getValue(String.class);
                    String City = snapshot.child("City").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String businessName = snapshot.child("businessName").getValue(String.class);
                    String carsToTreat = snapshot.child("carsToTreat").getValue(String.class);
                    String businessphone = snapshot.child("PhoneNumber").getValue(String.class);

                    editEmail.setText(email);
                    editBusinessName.setText(businessName);
                    editBusinessAddress.setText(address);
                    editBusinessPhone.setText(businessphone);
                    finalListOfCarBusiness = carsToTreat;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editBusinessCarsToTreat:
                listOfCarsForBusiness();
                break;
            case R.id.editBusinessProfileSave:
                changeBusinessProfile();
                break;

        }
    }

    private void changeBusinessProfile() {
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
                    values.put("Email", editEmail.getText().toString().trim());
                    values.put("City", businessCity.getSelectedItem().toString().trim());
                    values.put("KindOfBusiness", kindOfBusiness.getSelectedItem().toString().trim());
                    values.put("PhoneNumber", editBusinessPhone.getText().toString().trim());
                    values.put("address",editBusinessAddress.getText().toString().trim());
                    values.put("businessName", editBusinessName.getText().toString().trim());
                    values.put("carsToTreat",finalListOfCarBusiness);
                    if(!values.isEmpty()) {
                        user.updateEmail(editEmail.getText().toString().trim());
                        userRef.child(identifier).updateChildren(values, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Intent intent = new Intent(EditBusinessProfile.this, ProfileScreenBusiness.class);
                                Toast.makeText(EditBusinessProfile.this, "Profile updated", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(EditBusinessProfile.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(EditBusinessProfile.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void listOfCarsForBusiness() {
        listOfCars = getResources().getStringArray(R.array.CarTypesForBusiness);
        checkCars = new boolean[listOfCars.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditBusinessProfile.this);
        mBuilder.setTitle("Choose companies you treat");
        mBuilder.setMultiChoiceItems(listOfCars, checkCars, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if(isChecked) {
                    if (!carsBussines.contains(position)) {
                        carsBussines.add(position);
                    }
                }else if (carsBussines.contains(position)) {
                    carsBussines.remove(position);
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String finalListCars = "";
                for (int i = 0 ; i < carsBussines.size();i++){
                    finalListOfCarBusiness = finalListOfCarBusiness + listOfCars[carsBussines.get(i)];
                    if( i != carsBussines.size()-1){
                        finalListOfCarBusiness = finalListOfCarBusiness + ", ";
                    }
                }
            }
        });
        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i = 0; i<checkCars.length;i++){
                    checkCars[i] = false;
                    carsBussines.clear();
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}