package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyTreatment").
                setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Reminder - CarFix")
                .setContentText("you have appointment in one hour!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManget = NotificationManagerCompat.from(context);

        notificationManget.notify(200,builder.build());
    }
}
