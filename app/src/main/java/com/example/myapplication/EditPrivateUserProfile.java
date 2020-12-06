package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class EditPrivateUserProfile extends AppCompatActivity implements View.OnClickListener {

    public EditText editEmail,editPassword,editCarNumber,editCarYear;
    public Spinner CarType;
    public Button Save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_private_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();




        editEmail = findViewById(R.id.EDITEMAILPRIVATE);
        editPassword = findViewById(R.id.EDITPASSWORDPRIVATE);
        editCarNumber = findViewById(R.id.EDITCARNUMBERPRIVATE);
        editCarYear = findViewById(R.id.EDITCARYEARPRIVATE);
        //ADD EDIT TO SPINNER CARTYPE


        CarType = (Spinner) findViewById(R.id.EDITCARTYPEPRIVATE);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.CarTypes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CarType.setAdapter(myAdapter);

        Save = (Button) findViewById(R.id.saveEditPrivateProfile);
        Save.setOnClickListener(this);

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("PrivateUsers");

        userRef.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String Password = snapshot.child("Password").getValue(String.class);
                    String carYear = snapshot.child("CarYear").getValue(String.class);
                    String carNumber = snapshot.child("CarNumber").getValue(String.class);
                    String carType = snapshot.child("carCompany").getValue(String.class);

                    editEmail.setText(email);
                    editPassword.setText(Password);
                    editCarNumber.setText(carNumber);
                    editCarYear.setText(carYear);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.saveEditPrivateProfile:
                changePrivateProfile();
                break;
        }
    }



    private void changePrivateProfile() {
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
                    values.put("email", editEmail.getText().toString().trim());
                    values.put("Password", editPassword.getText().toString().trim());
                    values.put("CarNumber", editCarNumber.getText().toString().trim());
                    values.put("CarYear", editCarYear.getText().toString().trim());
                    values.put("carCompany", CarType.getSelectedItem().toString().trim());
                    if (!values.isEmpty()) {
                        user.updateEmail(editEmail.getText().toString().trim());
                        user.updatePassword(editPassword.getText().toString().trim());
                        userRef.child(identifier).updateChildren(values, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Intent intent = new Intent(EditPrivateUserProfile.this, ProfileScreenPrivate.class);
                                Toast.makeText(EditPrivateUserProfile.this, "Profile updated", Toast.LENGTH_LONG).show();

                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(EditPrivateUserProfile.this, "Cant Save new data", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}