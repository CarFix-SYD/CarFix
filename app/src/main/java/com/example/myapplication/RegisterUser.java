package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class registerUser extends AppCompatActivity implements View.OnClickListener {

    private Button registerUser;
    //for user register
    private EditText editTextEmail,editTexPassword,carNumber,carYear;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner carCompanyList;

    private TextView privateUser,businessUser;// choose which user to register

    //for business user
    private EditText editBusinessName,editBussinesPassword,editBusinessAddress,editBusinessEmail,editBusinesPhone;
    private Spinner kindOfBusiness,businessCity;

    private Button carsInBusinessButton;

    private  String [] listOfCars;
    private boolean [] checkCars;
    private ArrayList<Integer> carsBussines;
    private boolean WhichUser = true;
    private String finalListOfCarBusiness = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //set the private user button
        privateUser = (TextView) findViewById(R.id.PrivateUser);
        privateUser.setOnClickListener(this);

        //set the business user button
        businessUser = (TextView) findViewById(R.id.BusinessUser);
        businessUser.setOnClickListener(this);

        // initialize the Firebase variable
        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button)findViewById(R.id.registerbutton);
        registerUser.setOnClickListener(this);

        // initialize private user register
        editTextEmail = (EditText) findViewById(R.id.Email);

        editTexPassword = (EditText) findViewById(R.id.password);

        carNumber = (EditText) findViewById(R.id.carNumber);

        carYear = (EditText) findViewById(R.id.CarYear);
        carCompanyList = (Spinner) findViewById(R.id.carModelScrollDown);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.CarTypes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carCompanyList.setAdapter(myAdapter);




        //initialize business register
        editBusinessName = (EditText) findViewById(R.id.businessName);
        editBusinessName.setVisibility(View.GONE);

        editBusinessEmail = (EditText) findViewById(R.id.businessEmail);
        editBusinessEmail.setVisibility(View.GONE);

        editBussinesPassword = (EditText) findViewById(R.id.businessPassword);
        editBussinesPassword.setVisibility(View.GONE);

        kindOfBusiness = (Spinner) findViewById(R.id.kindOfBusiness);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.KindOfBusiness));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindOfBusiness.setAdapter(myAdapter1);
        kindOfBusiness.setVisibility(View.GONE);

        //Cars to treat in BUsiness the final array is carsBusiness
        carsInBusinessButton = (Button) findViewById(R.id.CarsInBusiness);
        carsInBusinessButton.setVisibility(View.GONE);
        carsInBusinessButton.setOnClickListener(this);
        carsBussines = new ArrayList<Integer>();


        //business address
        editBusinessAddress = (EditText) findViewById(R.id.businessAddress);
        editBusinessAddress.setVisibility(View.GONE);

        businessCity = (Spinner) findViewById(R.id.CitySpinner);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessCity.setAdapter(myAdapter2);
        businessCity.setVisibility(View.GONE);

        //business Phone number
        editBusinesPhone = (EditText) findViewById(R.id.businessPhoneNumber);
        editBusinesPhone.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.registerbutton://There is 2 kinds of Users and 2 kinds of regiseters
                if(WhichUser == true) {
                    registerPrivateUser();
                    break;
                }else {
                    registerBusinessUser();
                    break;
                }
            case R.id.PrivateUser:
                setPrivateUserVisible();
                break;
            case R.id.BusinessUser:
                setBussinesVaribalesVisible();
                break;
            case R.id.CarsInBusiness:
                listOfCarsForBusiness();
                break;
        }

    }

    /**
     * register fot business users
     * include Email,Business name,password for the user,garage,car that can be treated
     * and address. all this information is stored in firebase in bussinessUser branch
     */
    private void registerBusinessUser() {
        String email = editBusinessEmail.getText().toString().trim();
        String businessName = editBusinessName.getText().toString().trim();
        String password = editBussinesPassword.getText().toString().trim();
        String carToTreat = finalListOfCarBusiness;//
        String KinfOfBusiness = kindOfBusiness.getSelectedItem().toString().trim();
        String address = editBusinessAddress.getText().toString().trim();
        String city = businessCity.getSelectedItem().toString().trim();
        String phonenumber  = editBusinesPhone.getText().toString().trim();
        if(email.isEmpty()){
            editBusinessEmail.setError("Please enter E-mail");
            editBusinessEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editBusinessEmail.setError("Please enter valid E-mail");
            editBusinessEmail.requestFocus();
            return;
        }

        if(password.isEmpty() || password.length()<6){
            editTexPassword.setError("Enter 6 or more characters");
            editTexPassword.requestFocus();
        }

        if(KinfOfBusiness.isEmpty()){
            Toast.makeText(this, "Choose kind of business", Toast.LENGTH_SHORT).show();
        }

        if(finalListOfCarBusiness.isEmpty()){
            Toast.makeText(this,"choose cars companies",Toast.LENGTH_LONG).show();
        }

        if(address.isEmpty()){
            editBusinessAddress.setError("Please enter address");
            editBusinessAddress.requestFocus();
        }

        if(businessName.isEmpty()){
            editBusinessName.setError("Please enter address");
            editBusinessName.requestFocus();
        }

        if(city.isEmpty()){
            Toast.makeText(this, "Choose city of business", Toast.LENGTH_SHORT).show();
        }

        if(phonenumber.length() < 9 ){
            editBusinesPhone.setError("Enter minimum 9 digit phone number");
            editBusinesPhone.requestFocus();
        }


        //validation if email is empty or not valid for example

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            businessUser Buser = new businessUser(email,businessName,password,KinfOfBusiness,carToTreat,address,city,phonenumber,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference("BusinessUsers").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(Buser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        /* Buser.setID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Toast.makeText(registerUser.this ,Buser.getID(),Toast.LENGTH_LONG).show();
*/
                                        Toast.makeText(com.example.myapplication.registerUser.this ,"User is registered successfully",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(com.example.myapplication.registerUser.this, loginActivity.class));
                                    }else{
                                        Toast.makeText(com.example.myapplication.registerUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(com.example.myapplication.registerUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    /**
     * register fot private users
     * include Email, password, car number, car year that can be treated.
     * all this information is stored in firebase in privateUser branch
     */
    private void registerPrivateUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTexPassword.getText().toString().trim();
        String carCompany = carCompanyList.getSelectedItem().toString().trim();
        String car_number = carNumber.getText().toString().trim();
        String car_year = carYear.getText().toString().trim();

        //checks for the inputs validation
        if(email.isEmpty()){
            editTexPassword.setError("Please enter E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter valid E-mail");
            editTextEmail.requestFocus();
            return;
        }

        if(carCompany.isEmpty()){
            Toast.makeText(this,"You need to choose car company",Toast.LENGTH_LONG).show();

        }

        if(car_year.isEmpty()){
            carYear.setError("Please enter car year");
            carYear.requestFocus();
        }

        if(car_number.length()>8 || car_number.length()<6){
            carNumber.setError("Please enter 7-8 digits");
            carNumber.requestFocus();
        }
        //

        //enter the information to the database
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            privateUser user = new privateUser(email,password,carCompany,car_number,car_year);

                            FirebaseDatabase.getInstance().getReference("PrivateUsers").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(com.example.myapplication.registerUser.this ,"User is registered successfully",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(com.example.myapplication.registerUser.this, loginActivity.class));
                                    }else{
                                        Toast.makeText(com.example.myapplication.registerUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(com.example.myapplication.registerUser.this ,"Failed to register User",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void listOfCarsForBusiness() {

        listOfCars = getResources().getStringArray(R.array.CarTypesForBusiness);
        checkCars = new boolean[listOfCars.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(com.example.myapplication.registerUser.this);
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

    private void setPrivateUserVisible() {
        editTextEmail.setVisibility(View.VISIBLE);
        editTexPassword.setVisibility(View.VISIBLE);
        carCompanyList.setVisibility(View.VISIBLE);
        carNumber.setVisibility(View.VISIBLE);
        carYear.setVisibility(View.VISIBLE);
        WhichUser = true;// to register the private user

        editBusinessName.setVisibility(View.GONE);
        editBussinesPassword.setVisibility(View.GONE);
        kindOfBusiness.setVisibility(View.GONE);
        carsInBusinessButton.setVisibility(View.GONE);
        editBusinessAddress.setVisibility(View.GONE);
        editBusinessEmail.setVisibility(View.GONE);
        businessCity.setVisibility(View.GONE);
        editBusinesPhone.setVisibility(View.GONE);


    }

    private void setBussinesVaribalesVisible() {
        editBusinessEmail.setVisibility(View.VISIBLE);
        editBusinessName.setVisibility(View.VISIBLE);
        editBussinesPassword.setVisibility(View.VISIBLE);
        kindOfBusiness.setVisibility(View.VISIBLE);
        carsInBusinessButton.setVisibility(View.VISIBLE);
        editBusinessAddress.setVisibility(View.VISIBLE);
        businessCity.setVisibility(View.VISIBLE);
        editBusinesPhone.setVisibility(View.VISIBLE);
        WhichUser = false;// to register the Business user

        editTextEmail.setVisibility(View.GONE);
        editTexPassword.setVisibility(View.GONE);
        carCompanyList.setVisibility(View.GONE);
        carNumber.setVisibility(View.GONE);
        carYear.setVisibility(View.GONE);
    }

}