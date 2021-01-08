package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * this class show the business profile in two ways:
 * 1.to business user it shows editable version
 * 2.to private users it show read only version
 */
public class businessPageByUser extends AppCompatActivity implements View.OnClickListener {

    public EditText editEmail,editBusinessName,editBusinessAddress,editBusinessPhone;
    public TextView email, businessName,businessAddress,businessPhone,businessKind, businessCity;
    public String Semail, SbusinessName,SbusinessAddress,SbusinessPhone,SbusinessKind, SCity, ScarsToTreat;
    public Spinner kindOfBusiness,businessCitySpinner;
    public Button saveChanges;
    private Button carsInBusinessButton;
    private String[] listOfCars;
    private boolean[] checkCars;
    private ArrayList<Integer> carsBussines;
    private boolean WhichUser = true;
    private String finalListOfCarBusiness ="";
    private String newCarsInBusiNESS = "";
    public String BusinessID;
    public ImageView BImage, ImageFromPrivate;
    public  Uri selectedImage;

    public Button BookTreatment;

    FirebaseStorage storage;
    StorageReference storageReference;
    private static final String TAG = "businessPageByUser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        BusinessID = intent.getStringExtra("extraID");

        //create the firebase connection
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid(); // get the current user id
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootref.child("BusinessUsers");

        // get all information from the business user database
        userRef.child(BusinessID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Semail = snapshot.child("Email").getValue().toString().trim();
                    SbusinessKind = snapshot.child("KindOfBusiness").getValue(String.class);
                    SCity = snapshot.child("City").getValue(String.class);
                    SbusinessAddress = snapshot.child("address").getValue(String.class);
                    SbusinessName = snapshot.child("businessName").getValue(String.class);
                    ScarsToTreat = snapshot.child("carsToTreat").getValue(String.class);
                    SbusinessPhone = snapshot.child("PhoneNumber").getValue(String.class);
                    finalListOfCarBusiness = snapshot.child("carsToTreat").getValue(String.class);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        //both references down are to check if the user connected to this activity is private or business
        //if private go to isPrivate() if business go to isBusiness()
        DatabaseReference jloginDatabaseBusiness = FirebaseDatabase.getInstance().getReference("/BusinessUsers/").child(currentuid);
        jloginDatabaseBusiness.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("type").getValue().toString();
                    if (userType.equals("BusinessUser")) {
                        setContentView(R.layout.activity_edit_business_profile);
                        isBusiness();
                    } else {
                        Toast.makeText(businessPageByUser.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference jloginDatabasePrivate = FirebaseDatabase.getInstance().getReference("/PrivateUsers/").child(currentuid);
        jloginDatabasePrivate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userType = snapshot.child("type").getValue().toString();
                    if (userType.equals("PrivateUser")) {
                        setContentView(R.layout.business_page_from_private);
                        isPrivate();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * create the editable version of the activity for the business user
     * here he can edit all the attributes in his profile except the password
     */
    public void isBusiness(){

        saveChanges = (Button) findViewById(R.id.editBusinessProfileSave);
        saveChanges.setOnClickListener(this);

        carsInBusinessButton = (Button) findViewById(R.id.editBusinessCarsToTreat);
        carsInBusinessButton.setOnClickListener(this);
        carsBussines = new ArrayList<Integer>();

        editBusinessName = (EditText) findViewById(R.id.editBusinessName);
        editBusinessName.setText(SbusinessName);

        editEmail = (EditText) findViewById(R.id.editBusinessEmail);
        editEmail.setText(Semail);

        kindOfBusiness = (Spinner) findViewById(R.id.spinerBusinessKind);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.KindOfBusiness));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindOfBusiness.setAdapter(myAdapter1);
        kindOfBusiness.setSelection(myAdapter1.getPosition(SbusinessKind));

        //business address
        editBusinessAddress = (EditText) findViewById(R.id.editBusinessAddress);
        editBusinessAddress.setText(SbusinessAddress);

        businessCitySpinner = (Spinner) findViewById(R.id.spinnerBusinessCity);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Cities));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessCitySpinner.setAdapter(myAdapter2);
        businessCitySpinner.setSelection(myAdapter2.getPosition(SCity));

        //business Phone number
        editBusinessPhone = (EditText) findViewById(R.id.editBusinessPhone);
        editBusinessPhone.setText(SbusinessPhone);

        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        BImage = (ImageView)findViewById(R.id.imageView2);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGalleryIntent, 100);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.getData();
            BImage.setImageURI(selectedImage);
            uploadImage();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(selectedImage).build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
        }
    }

    private void uploadImage() {
        if (selectedImage != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(
                        UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(businessPageByUser.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(businessPageByUser.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(
                        UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }


    /**
     * isPrivate() -create the read only version for the private user
     */
    public void isPrivate() {

        ImageFromPrivate = findViewById(R.id.imageViewFromPrivate);
        ImageFromPrivate.setImageURI(selectedImage);

        businessName = (TextView)findViewById(R.id.textBN);
        businessName.setText(SbusinessName);

        email = (TextView)findViewById(R.id.textEmail);
        email.setText(Semail);


        businessKind = (TextView)findViewById(R.id.textKind);
        businessKind.setText(SbusinessKind);

        //business address
        businessAddress =  (TextView)findViewById(R.id.textAddress);
        businessAddress.setText(SbusinessAddress);
        businessCity = (TextView) findViewById(R.id.textCity);
        businessCity.setText(SCity);

        //business Phone number
        businessPhone = (TextView)findViewById(R.id.textPhone);
        businessPhone.setText(SbusinessPhone);

        BookTreatment = (Button) findViewById(R.id.BOOKTREATMENT);
        BookTreatment.setOnClickListener(this);



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
            case R.id.BOOKTREATMENT:
                Intent intent = new Intent(this, com.example.myapplication.BookTreatment.class);
                intent.putExtra("BID",BusinessID);
                startActivity(intent);
                break;
        }
    }
    //save the chnages
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
                    values.put("City", businessCitySpinner.getSelectedItem().toString().trim());
                    values.put("KindOfBusiness", kindOfBusiness.getSelectedItem().toString().trim());
                    values.put("PhoneNumber", editBusinessPhone.getText().toString().trim());
                    values.put("address",editBusinessAddress.getText().toString().trim());
                    values.put("businessName", editBusinessName.getText().toString().trim());
                    if(newCarsInBusiNESS.equals(finalListOfCarBusiness)){
                        values.put("carsToTreat",finalListOfCarBusiness);
                    }else if(newCarsInBusiNESS.isEmpty()){
                        values.put("carsToTreat",finalListOfCarBusiness);
                    }else{
                        values.put("carsToTreat",newCarsInBusiNESS);
                    }

                    if(!values.isEmpty()) {
                        user.updateEmail(editEmail.getText().toString().trim());
                        userRef.child(identifier).updateChildren(values, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Intent intent = new Intent(businessPageByUser.this, ProfileScreenBusiness.class);
                                Toast.makeText(businessPageByUser.this, "Profile updated", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(businessPageByUser.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(businessPageByUser.this,"Cant Save new data",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // create the list of cars to treat for the business user to add or change
    private void listOfCarsForBusiness() {
        listOfCars = getResources().getStringArray(R.array.CarTypesForBusiness);
        checkCars = new boolean[listOfCars.length];
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(businessPageByUser.this);
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
                    newCarsInBusiNESS = newCarsInBusiNESS + listOfCars[carsBussines.get(i)];
                    if( i != carsBussines.size()-1){
                        newCarsInBusiNESS = newCarsInBusiNESS + ", ";
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