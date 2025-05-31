package com.example.capsular.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.capsular.Activity.HomeActivity;
import com.example.capsular.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CapsuleNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "capsule_reminder_channel";
    private static final String TAG = "CapsuleNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String capsuleDate = intent.getStringExtra("capsule_date");
        int notificationId = capsuleDate != null ? capsuleDate.hashCode() : 100; // Unique ID based on date

        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Capsule Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminds you to open or create a capsule");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_capsule)
                .setContentTitle("⏳ Time Capsule Reminder")
                .setContentText("Your capsule for " + capsuleDate + " is ready to open!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        } else {
            Log.e(TAG, "❌ NotificationManager is null");
        }

        // Save notification to history
        SharedPreferences prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);
        String history = prefs.getString("notification_history", "");
        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        history += "Capsule Ready for " + capsuleDate + "::" + date + ";;";
        prefs.edit().putString("notification_history", history).putBoolean("hasUnread", true).apply();

        Log.d(TAG, "✅ Notification triggered for " + capsuleDate + ", ID: " + notificationId);
    }
}