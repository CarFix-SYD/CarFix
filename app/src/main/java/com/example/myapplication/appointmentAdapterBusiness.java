package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class appointmentAdapterBusiness extends BaseAdapter {

    private Activity context;
    ArrayList<Appointment> appointments;// gets list of appointments of the private user
    private static LayoutInflater inflater = null;
    private DatabaseReference dref = FirebaseDatabase.getInstance().getReference();


    // build the adapter for the screen items
    public appointmentAdapterBusiness(Activity context, ArrayList<Appointment> app) {
        this.context = context;
        this.appointments = app;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return this.appointments.size();
    }

    @Override
    public Appointment getItem(int position) {
        return this.appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Enter to each item in the view the exact information from the database.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_appointement_business, null) : itemView;
        TextView textViewTime = (TextView) itemView.findViewById(R.id.app_Date);
        TextView textViewPrivateName = (TextView) itemView.findViewById(R.id.privateAppName);
        TextView textViewCar = (TextView) itemView.findViewById(R.id.privateCar);
        TextView textViewCarNumber = (TextView) itemView.findViewById(R.id.privateCarNumber);

        Appointment selected = appointments.get(position);
        dref.child("PrivateUsers").child(selected.getPrivateID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewTime.setText(selected.getDate().replace("e", ""));
                textViewPrivateName.setText(snapshot.child("email").getValue().toString().trim());
                textViewCar.setText(snapshot.child("carCompany").getValue().toString().trim());
                textViewCarNumber.setText(snapshot.child("CarNumber").getValue().toString().trim());


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        Button leaveDescription = (Button) itemView.findViewById(R.id.setDescription);
        leaveDescription.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openDescriptionDialog(selected.getBusinessID(), selected.getPrivateID(), textViewPrivateName.getText().toString(),selected.getDate());
            }
        });
        return itemView;
    }


    public void openDescriptionDialog(String businessID,String userID,String businessName,String date) {
        addDescriptionBsuinessDialog descDialog = new addDescriptionBsuinessDialog(businessID,userID,businessName,date);
        descDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"H");
    }


}

