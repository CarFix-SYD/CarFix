package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookTreatment extends AppCompatActivity implements View.OnClickListener{

    public CalendarView currentCalandar;
    public String date,time;
    public Button Book;
    public Spinner BookTime;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference dRefBusiness = database.getReference("BusinessUsers");
    public DatabaseReference dRefPrivate = database.getReference("PrivateUsers");
    public String BusinessId,userId ;
    public ArrayList<String> problematicHours = new ArrayList<String>();

    public String Pshecuale = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_treatment);

        //ID BUSINESS
        Intent intent = getIntent();
        BusinessId = intent.getStringExtra("BID");
        //ID USER
        //
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        BookTime = (Spinner) findViewById(R.id.spinnerBookTime);
        List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Appointment_time)));
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BookTime.setAdapter(myAdapter);



        currentCalandar = (CalendarView) findViewById(R.id.calendarView);
        currentCalandar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                list.clear(); // clear the hours list
                list.addAll(Arrays.asList(getResources().getStringArray(R.array.Appointment_time))); // add the hours list again
                myAdapter.notifyDataSetChanged();//notify thr adapter of the spinner
                problematicHours.clear();// clear previous problematic hours
                date = dayOfMonth + "/" + (month+1) + "/" + year;
                dRefBusiness.child(BusinessId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.child("BookedTreatment").exists()){
                            String BookBusinessToAdapter []= snapshot.child("BookedTreatment").getValue().toString().split("e");


                            for (int i = 0 ; i<BookBusinessToAdapter.length;i++){
                                if(BookBusinessToAdapter[i].substring(0,10).equals(date)){
                                    problematicHours.add(BookBusinessToAdapter[i].split(",")[1]);

                                }
                            }
                            for (int i = 0 ; i < problematicHours.size();i++){
                                list.remove(problematicHours.get(i));
                            }


                        }
                        myAdapter.notifyDataSetChanged();
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




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BookInCalander:
                saveDateAndTime();
                break;
        }
    }

    private void saveDateAndTime() {
        String spinnerselection = BookTime.getSelectedItem().toString().trim();
        String toPush = date + "," + spinnerselection + "e";
        Boolean start = true;

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


        if(start) {
            dRefBusiness.child(BusinessId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //check if Bussines user have treatment
                    if (snapshot.exists() && snapshot.child("BookedTreatment").exists()) {
                        String businessScheduale = snapshot.child("BookedTreatment").getValue().toString().trim();
                        //if exist then split it to make array to ckeck inside
                        if (!businessScheduale.isEmpty()) {
                            // if not push it to the databse of the business user
                            Map<String, Object> values = new HashMap<>();
                            values.put("BookedTreatment", toPush + businessScheduale);
                            if (!values.isEmpty()) {

                                dRefBusiness.child(BusinessId).updateChildren(values, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                        dRefPrivate.child(userId).child("BookedTreatment").setValue(toPush + Pshecuale);//add to the User firebase
                                        Toast.makeText(BookTreatment.this, "Treatment saved in date "+toPush , Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(BookTreatment.this, ProfileScreenPrivate.class);
                                        startActivity(intent);

                                    }
                                });
                            }

                        }

                    } else {
                        dRefBusiness.child(BusinessId).child("BookedTreatment").setValue(toPush).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BookTreatment.this, "Treatment saved in date "+toPush , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        dRefPrivate.child(userId).child("BookedTreatment").setValue(toPush).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(BookTreatment.this, "Treatment saved in date "+toPush , Toast.LENGTH_LONG).show();
                               Intent intent = new Intent(BookTreatment.this, ProfileScreenPrivate.class);
                                startActivity(intent);
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });
        }
    }




    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



}

