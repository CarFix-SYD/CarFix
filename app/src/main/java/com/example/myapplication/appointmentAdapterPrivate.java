package com.example.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class appointmentAdapterPrivate extends BaseAdapter  {

    private Activity context;
    ArrayList<Appointment> appointments;// gets list of appointments of the private user
    private static LayoutInflater inflater = null;
    private DatabaseReference dref = FirebaseDatabase.getInstance().getReference();


    // build the adapter for the screen items
    public appointmentAdapterPrivate(Activity context, ArrayList<Appointment> app) {
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
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_appointment_private, null) : itemView;
        TextView textViewTime = (TextView) itemView.findViewById(R.id.treatmentTime);
        TextView textViewBusinessName = (TextView) itemView.findViewById(R.id.treatmentBusiness);
        TextView textViewAddress = (TextView) itemView.findViewById(R.id.treatmentAddress);
        TextView textViewPhone = (TextView) itemView.findViewById(R.id.treatmentPhone);
        TextView textViewDescription = (TextView) itemView.findViewById(R.id.treatmentDescription);
        Appointment selected = appointments.get(position);
        dref.child("BusinessUsers").child(selected.getBusinessID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewTime.setText(selected.getDate().replace("e", ""));
                textViewBusinessName.setText(snapshot.child("businessName").getValue().toString().trim());
                textViewAddress.setText(snapshot.child("address").getValue().toString().trim() + "," + snapshot.child("City").getValue().toString().trim());
                textViewPhone.setText(snapshot.child("PhoneNumber").getValue().toString().trim());
                textViewDescription.setText(selected.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //text view on click listener for calling to the garage.
        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog
                AlertDialog questionCallPhone = new AlertDialog.Builder(context).
                        setTitle("Do you want to move to dialer?").
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //set the number in the phone caller
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + textViewPhone.getText().toString()));
                                context.startActivity(intent);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.closeContextMenu();
                    }
                }).create();
                questionCallPhone.show();
            }


        });


            //Cancel button for the app in the list view
            Button CancelApp = (Button) itemView.findViewById(R.id.cancelApp);
            CancelApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog questionLogOut = new AlertDialog.Builder(context).
                            setTitle("Are you shore you want to delete this appointment?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteAppFromDB(selected.getBusinessID(),selected.getPrivateID(),selected.getDate());
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.closeContextMenu();
                        }
                    }).create();
                    questionLogOut.show();
                    }
            });

            //Leave review for the business
            Button leaveReview = (Button) itemView.findViewById(R.id.privateReview);
            leaveReview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    openReviewDialog(selected.getBusinessID(), selected.getPrivateID(), textViewBusinessName.getText().toString());
                }
            });

            //Set notification button
            createNotificationChannel();
            Button setNotif = (Button) itemView.findViewById(R.id.setNotification);
            setNotif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(convertToMillis(selected.getDate()) > System.currentTimeMillis()) {
                        Toast.makeText(context,"Reminder Set!",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(context,ReminderBroadcast.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);

                        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        try {
                            //set the reminder notification 1 hour before appointment
                                long timeToNotify = convertToMillis(selected.getDate()) - (1000 * 3600);
                                alarmManager.set(alarmManager.RTC_WAKEUP, timeToNotify, pendingIntent);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        }else{
                            Toast.makeText(context,"Cant set notification to past appointment",Toast.LENGTH_LONG).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            return itemView;
    }



    public void deleteAppFromDB(String businessID,String userID,String date){
        DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference("BusinessUsers/"+businessID + "/BookedTreatment/");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("PrivateUsers/"+userID + "/BookedTreatment/");
        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference("Appointments/");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String keyTreatment = "";
                if (snapshot.exists()) {
                    for (DataSnapshot treatmentKey : snapshot.getChildren()) {
                        if(treatmentKey.child("date").getValue().toString().equals(date) && treatmentKey.child("businessID").getValue().toString().equals(businessID) && treatmentKey.child("privateID").getValue().toString().equals(userID)){
                            keyTreatment = treatmentKey.getKey();

                            businessRef.child(keyTreatment).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                }
                            });
                            userRef.child(keyTreatment).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                }
                            });
                            appRef.child(keyTreatment).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                }
                            });

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void openReviewDialog(String businessID,String userID,String businessName) {
        leaveReviewDialog reviewDialog = new leaveReviewDialog(businessID,userID,businessName);
        reviewDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"H");
    }


    public void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "notifyTreatment";
            String description = "Channel for treatment notify";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("notifyTreatment",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private long convertToMillis(String toString) throws ParseException {
        String toConvertDate = toString.split("e")[0];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
        Date date = sdf.parse(toConvertDate);
        return date.getTime();
    }


}

