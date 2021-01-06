package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class leaveReviewDialog extends AppCompatDialogFragment {
    private EditText review;
    private RatingBar ratingBusiness;
    private leaveReivewInterface listener;
    private String businessID,userID,businessName;

    public leaveReviewDialog(String businessID, String userID,String businessName) {
        this.businessID = businessID;
        this.userID = userID;
        this.businessName = businessName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_leave_review_dialog, null);
        review = view.findViewById(R.id.leaveReviewEditText);
        ratingBusiness = view.findViewById(R.id.ratingBarReview);
        builder.setView(view)
                .setTitle("Leave review to: " + this.businessName )
                .setNegativeButton("cancel", (dialogInterface, i) -> {
                })
                .setPositiveButton("Save changes", (dialogInterface, i) -> {
                    String reviewString = review.getText().toString();
                    listener.saveReivew(reviewString,this.businessID,this.userID,String.valueOf(ratingBusiness.getRating()));
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (leaveReivewInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface leaveReivewInterface {
        void saveReivew(String review,String businessID,String userID,String ratingValue);

    }
}