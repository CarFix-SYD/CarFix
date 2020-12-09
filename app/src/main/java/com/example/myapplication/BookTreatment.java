package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

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
    public String BusinessId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_treatment);

        //ID BUSINESS
        Intent intent = getIntent();
        BusinessId = intent.getStringExtra("BID");
        //ID USER
        //
        currentCalandar = (CalendarView) findViewById(R.id.calendarView);
        currentCalandar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/"+month+"/"+year;
                Toast.makeText(BookTreatment.this,date,Toast.LENGTH_LONG).show();

            }
        });

        Book = (Button) findViewById(R.id.BookInCalander);
        Book.setOnClickListener(this);

        List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Appointment_time)));
        BookTime = (Spinner) findViewById(R.id.spinnerBookTime);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BookTime.setAdapter(myAdapter);


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
        long milliDate = currentCalandar.getDate();

        //date = getDate(milliDate,"dd/MM/yyyy");
        String spinnerselection = BookTime.getSelectedItem().toString().trim();

        dRefBusiness.child(BusinessId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String toPush = date + "," + spinnerselection + "e";

                if (snapshot.exists() && snapshot.child("BookedTreatment").exists()) {
                    String s = snapshot.child("BookedTreatment").getValue().toString().trim();

                    if (s != "" ) {
                    String[] checkDates = s.split("e");

                        for (String check : checkDates) {
                            if (check.equals(toPush)) {
                                Toast.makeText(BookTreatment.this, "Allready taken", Toast.LENGTH_LONG).show();
                            } else {
                                Map<String, Object> values = new HashMap<>();
                                values.put("BookedTreatment", toPush);
                                if (!values.isEmpty()) {
                                    dRefBusiness.child(BusinessId).updateChildren(values, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(BookTreatment.this, "Saved Treatment", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(BookTreatment.this, ProfileScreenPrivate.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }

                    }
                }else{
                    dRefBusiness.child(BusinessId).child("BookedTreatment").push().setValue(toPush);
                }

            }
        @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

