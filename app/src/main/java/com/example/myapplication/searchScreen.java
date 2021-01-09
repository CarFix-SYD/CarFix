package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchScreen extends AppCompatActivity implements View.OnClickListener{

    private Spinner kindOfTreatment,carCompany,businessCity;
    private TextView textViewProfileButton;
    private TextView profileBtn;
    private Button searchButton;
    public ImageButton backToProfile;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        //Need to add the name of the user to this text view
        textViewProfileButton = (TextView) findViewById(R.id.UserProfile);
        textViewProfileButton.setOnClickListener(this);

        backToProfile = (ImageButton) findViewById(R.id.backToProfile);
        backToProfile.setOnClickListener(this);


        kindOfTreatment = (Spinner) findViewById(R.id.kindOfTreat);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.KindOfBusiness));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindOfTreatment.setAdapter(myAdapter);


        carCompany = (Spinner) findViewById(R.id.carCompany);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.CarTypes));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carCompany.setAdapter(myAdapter1);



        businessCity = (Spinner) findViewById(R.id.searchCitySpinner);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessCity.setAdapter(myAdapter2);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        String profileName =getIntent().getStringExtra("profileName");
        profileBtn = (TextView) findViewById(R.id.UserProfile);
        profileBtn.setText(profileName);
        profileBtn.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference("BusinessUsers");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.UserProfile:
                startActivity(new Intent(this, ProfileScreenPrivate.class));
                break;
            case R.id.searchButton:
                SearchTreatment();
                break;
            case R.id.backToProfile:
                Intent BackToSearchintent = new Intent(this, ProfileScreenPrivate.class);
                startActivity(BackToSearchintent);
                break;

        }

    }

    private void SearchTreatment() {
        String KindOfTreat = kindOfTreatment.getSelectedItem().toString().trim();
        String CarCompany = carCompany.getSelectedItem().toString().trim();
        String searchCitySpinner = businessCity.getSelectedItem().toString().trim();

        ArrayList<businessUser> listBusiness = new ArrayList<businessUser>();
        listBusiness.clear();
        mDatabase.addValueEventListener(new ValueEventListener() {
            //onDataChange is Asynchronous so all the methods as to be inside otherwise
            //it will not get the data outside the function
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()){

                        String searchEmail = data.child("Email").getValue().toString().trim();
                        String searchKindOfBusiness = data.child("KindOfBusiness").getValue().toString().trim();
                        String searchPassword = data.child("Password").getValue().toString().trim();
                        String searchAdrdess = data.child("address").getValue().toString().trim();
                        String searchbusinessName = data.child("businessName").getValue().toString().trim();
                        String searchCarsToTreat = data.child("carsToTreat").getValue().toString().trim();
                        String searchbusinessCity = data.child("City").getValue().toString().trim();
                        String searchbusinessPhone = data.child("PhoneNumber").getValue().toString().trim();
                        String searchbusinessID = data.child("ID").getValue().toString().trim();

                        if(searchKindOfBusiness.equals(KindOfTreat) && doesContain(searchCarsToTreat,CarCompany) && searchCitySpinner.equals(searchbusinessCity)) {
                            businessUser Buser;
                            Buser = new businessUser(searchEmail, searchbusinessName, searchPassword, searchKindOfBusiness, searchCarsToTreat, searchAdrdess,searchbusinessCity,searchbusinessPhone, searchbusinessID);
                            listBusiness.add(Buser);

                        }
                }

                if(listBusiness.size()!=0) {
                    Intent myIntent = new Intent(searchScreen.this,searchResults.class);
                    myIntent.putExtra("businessList",listBusiness);
                    startActivity(myIntent);
                }
                else{
                    Toast.makeText(searchScreen.this,"No results",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean doesContain(String searchCarsToTreat, String carCompany) {
        String [] List = searchCarsToTreat.split(", ");
        for (String s : List){
            if(s.equals(carCompany))
                return true;
        }
        return false;
    }
}