package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * businessAdapterForUserResultSearch - this class represents the item in the list of the search results
 * to every result it creates item view with all the attributes.
 */
public class businessAdapterForUserResultSearch extends BaseAdapter {
    private Activity context;
    ArrayList<businessUser> business;// gets list of business users from the search
    private static LayoutInflater inflater = null;
    private DatabaseReference bRef;

    // build the adapter for the screen items
    public businessAdapterForUserResultSearch(Activity context, ArrayList<businessUser> business) {
        this.context = context;
        this.business = business;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return this.business.size();
    }

    @Override
    public businessUser getItem(int position) {
        return this.business.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Enter to each item in the view the exact information from the database.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View itemView = convertView;
       itemView = (itemView == null) ? inflater.inflate(R.layout.list_business,null): itemView;
       TextView textViewName = (TextView) itemView.findViewById(R.id.textViewName);
       TextView textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
       TextView textViewAddress= (TextView) itemView.findViewById(R.id.businessAddressResult);
       TextView textViewPhone = (TextView) itemView.findViewById(R.id.businessPhone);
       TextView textID = (TextView) itemView.findViewById(R.id.textviewID);
       TextView businessRatingBar = (TextView) itemView.findViewById(R.id.AVGrating);
       businessUser selected = business.get(position);
       textViewName.setText(selected.businessName);
       textViewEmail.setText(selected.Email);
       textViewAddress.setText(selected.City + ", " + selected.address);
       textViewPhone.setText(selected.PhoneNumber);
       textID.setText(selected.ID);
       textID.setVisibility(View.GONE);
       bRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+selected.ID);
       bRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child("AVGrating").exists()){
                   businessRatingBar.setText("Rating: " + snapshot.child("AVGrating").getValue().toString().trim());
                   Toast.makeText(context,snapshot.child("AVGrating").getValue().toString().trim(),Toast.LENGTH_LONG).show();
               }else{
                   businessRatingBar.setVisibility(View.GONE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


        Button moveToSelected = (Button) itemView.findViewById(R.id.buttonToBusiness);
        //the button "Order" in each item that direct to business ptofile
        moveToSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context, businessPageByUser.class);
                    intent.putExtra("extraID", textID.getText().toString().trim());
                    context.startActivity(intent);

            }
        });

       return itemView;
    }
}