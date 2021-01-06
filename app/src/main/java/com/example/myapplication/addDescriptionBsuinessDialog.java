package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatDialogFragment;

public class addDescriptionBsuinessDialog extends AppCompatDialogFragment {
    private EditText decription;
    private addDescriptionInterface listener;
    private String businessID,userID,businessName,date;

    public addDescriptionBsuinessDialog(String businessID, String userID,String businessName,String date) {
        this.businessID = businessID;
        this.userID = userID;
        this.businessName = businessName;
        this.date = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_description, null);
        decription = view.findViewById(R.id.addDescription);
        builder.setView(view)
                .setTitle("Add description: " )
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                })
                .setPositiveButton("Save changes", (dialogInterface, i) -> {
                    String descriptionString = decription.getText().toString();
                    listener.saveDescription(descriptionString,this.businessID,this.userID,this.date);
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (addDescriptionInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface addDescriptionInterface {
        void saveDescription(String Description,String businessID,String userID,String date);

    }
}

