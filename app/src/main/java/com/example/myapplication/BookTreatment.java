package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class BookTreatment is used to book treatments in business and private user database,
 * get the previous information from both and check the time available.
 */
public class BookTreatment extends AppCompatActivity implements View.OnClickListener{

    public CalendarView currentCalandar;
    public String date,time;
    public Button Book;
    public Spinner BookTime;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference dRefBusiness = database.getReference("BusinessUsers");
    public DatabaseReference dRefPrivate = database.getReference("PrivateUsers");
    public DatabaseReference dRefAppoint = database.getReference("Appointments");

    public String BusinessId,userId ;
    public ArrayList<String> problematicHours = new ArrayList<String>();



    public String Pshecuale = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_treatment);

        Intent intent = getIntent();
        BusinessId = intent.getStringExtra("BID");//get the Business id from the last screen

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();// get the currenrt user id from the database

        BookTime = (Spinner) findViewById(R.id.spinnerBookTime);
        List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Appointment_time)));
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BookTime.setAdapter(myAdapter);


        currentCalandar = (CalendarView) findViewById(R.id.calendarView);

        //listener for the calendar changes and check the time that available for the business in that day
        currentCalandar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                list.clear(); // clear the hours list
                list.addAll(Arrays.asList(getResources().getStringArray(R.array.Appointment_time))); // add the hours list again
                myAdapter.notifyDataSetChanged();//notify thr adapter of the spinner
                problematicHours.clear();// clear previous problematic hours
                date = (dayOfMonth > 10 ? dayOfMonth:"0"+dayOfMonth) + "/" + ((month+1) < 10 ? "0"+(month+1):(month+1)) + "/" + year;

                dRefBusiness.child(BusinessId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.child("BookedTreatment").exists()) {
                            String s = "";
                            for (DataSnapshot child : snapshot.child("BookedTreatment").getChildren()) {
                                if (child.exists()) {
                                   String key= child.getKey();
                                    s+=child.child("key").child("date").getValue().toString();

                                }
                            }

                            if (s.length() != 0) {
                                String BookBusinessToAdapter[] = s.split("e");
                                for (int i = 0; i < BookBusinessToAdapter.length; i++) {
                                    if (BookBusinessToAdapter[i].substring(0, 10).equals(date)) {
                                        problematicHours.add(BookBusinessToAdapter[i].split(",")[1]);//add the problematic hours for each business
                                    }
                                }
                                for (int i = 0; i < problematicHours.size(); i++) {
                                    list.remove(problematicHours.get(i));// remove them from the strings list
                                }
                                myAdapter.notifyDataSetChanged();//notify the adapter on the changes
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        Book = (Button) findViewById(R.id.BookInCalander);
        Book.setOnClickListener(this);

    }



    // on click wwaits for user click in the screen and get the id of the component clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BookInCalander:
                saveDateAndTime();
                break;
        }
    }




    /**
     * א.this function book the treatment in both users databases
     */
    private void saveDateAndTime() {
        String spinnerselection = BookTime.getSelectedItem().toString().trim();
        String toPush = date + "," + spinnerselection + "e";
        Boolean start = true;
        String key = dRefBusiness.push().getKey();

        Appointment newP = new Appointment(toPush,userId,BusinessId);
        // we check if the private user has previous schedule if there is save it in P schedule
        dRefPrivate.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.child("BookedTreatment").exists()) {
                    Pshecuale = snapshot.child("BookedTreatment").getValue().toString().trim();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // add listener to the business place in the database
        if(start) {
            dRefBusiness.child(BusinessId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //check if Business user have "BookedTreatment" in database
                    if (snapshot.exists() && snapshot.child("BookedTreatment").exists()) {


                        String businessScheduale = snapshot.child("BookedTreatment").getValue().toString().trim();
                        //if exist then split it to make array to ckeck inside
                        if (!businessScheduale.isEmpty()) {
                            // if not push it to the databse of the business user
                            Map<String, Object> values = new HashMap<>();
                            values.put(newP.getDate().replace("/","_"), newP);

                            if (!values.isEmpty()) {
                                // if the business user have "BookedTreatment" in database save the previous with the new one
                                dRefBusiness.child(BusinessId).child("BookedTreatment/"+key).updateChildren(values, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                        dRefPrivate.child(userId).child("BookedTreatment/"+key).updateChildren(values, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(BookTreatment.this, "Treatment saved in date " + toPush, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(BookTreatment.this, ProfileScreenPrivate.class);
                                                startActivity(intent);
                                            }
                                                    });

                                    }
                                });
                            }

                        }

                        //the else statement is for thr case when the business user dont have previous schedule,
                        // and here it add the key of "BookedTreatment" to the database and the date, for both users
                    } else {
                        dRefBusiness.child(BusinessId).child("BookedTreatment/"+key+"/").setValue(newP).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BookTreatment.this, "Treatment saved in date "+toPush , Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        //here we save the previous treatments and the new one in the private user database
                        dRefPrivate.child(userId).child("BookedTreatment/"+key+"/").setValue(newP).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(BookTreatment.this, "Treatment saved in date "+toPush , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BookTreatment.this, ProfileScreenPrivate.class);
                                startActivity(intent);
                            }
                        });

                    }
                    dRefAppoint.child(key).setValue(newP, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });
        }
    }



}

