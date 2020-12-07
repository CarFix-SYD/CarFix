package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class businessAdapter extends BaseAdapter {
    private Activity context;
    ArrayList<businessUser> business;
    private static LayoutInflater inflater = null;

    public businessAdapter(Activity context, ArrayList<businessUser> business) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View itemView = convertView;
       itemView = (itemView == null) ? inflater.inflate(R.layout.list_business,null): itemView;
       TextView textViewName = (TextView) itemView.findViewById(R.id.textViewName);
       TextView textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
       TextView textViewAddress= (TextView) itemView.findViewById(R.id.businessAddressResult);
       TextView textViewPhone = (TextView) itemView.findViewById(R.id.businessPhone);
       businessUser selected = business.get(position);
       textViewName.setText(selected.businessName);
       textViewEmail.setText(selected.Email);
       textViewAddress.setText(selected.City + ", " + selected.address);
       textViewPhone.setText(selected.PhoneNumber);


        Button moveToSelected = (Button) itemView.findViewById(R.id.buttonToBusiness);
        moveToSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditBusinessProfile.class);
                context.startActivity(intent);
            }
        });

       return itemView;
    }
}
