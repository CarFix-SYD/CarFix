package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileScreenBusiness extends AppCompatActivity implements View.OnClickListener, PasswordDialog.ExampleDialogListener{
    public TextView helloUser;
    public Button editProfile;
    public ImageButton settings;
    public FirebaseUser currentUser;
    public String RegisterdID;
    public String Path;
    public TextView editbusinessPassword;
    private AlertDialog alertDialog = null;



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

    }
    public void openDialog() {
        PasswordDialog exampleDialog = new PasswordDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editBusinessUser:
                Intent intent = new Intent(this,BusinessProfile.class);
                intent.putExtra("extraID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
                break;
            /*case R.id.searchFromBusinessProfile:
                startActivity(new Intent(this,searchScreen.class));
                break;*/

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
}