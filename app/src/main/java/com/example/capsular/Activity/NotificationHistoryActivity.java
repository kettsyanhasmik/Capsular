package com.example.capsular.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.capsular.R;

public class NotificationHistoryActivity extends AppCompatActivity {

    private LinearLayout notificationContainer;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        notificationContainer = findViewById(R.id.notification_container);

        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        String history = prefs.getString("notification_history", "");

        if (!history.isEmpty()) {
            String[] notifications = history.split(";;");
            for (String notif : notifications) {
                if (notif.trim().isEmpty()) continue;
                String[] parts = notif.split("::");
                if (parts.length >= 2) {
                    addNotificationToLayout(parts[0], parts[1]);
                } else {
                    android.util.Log.w("NotificationHistory", "Invalid notification format: " + notif);
                }
            }
            addClearButton();
        } else {
            TextView emptyView = new TextView(this);
            emptyView.setText("No notifications yet.");
            emptyView.setTextSize(16);
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setGravity(Gravity.CENTER);
            notificationContainer.addView(emptyView);
        }

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearUnreadStatus();
    }

    private void clearUnreadStatus() {
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        prefs.edit().putBoolean("hasUnread", false).apply();
    }

    private void addNotificationToLayout(String title, String date) {
        TextView notifView = new TextView(this);
        notifView.setText("\uD83D\uDCEC " + title + " (" + date + ")");
        notifView.setTextSize(16);
        notifView.setPadding(16, 16, 16, 16);
        notificationContainer.addView(notifView);
    }

    private void addClearButton() {
        clearButton = new Button(this);
        clearButton.setText("Clear All Notifications");
        clearButton.setTextColor(getResources().getColor(android.R.color.white));
        clearButton.setBackgroundResource(R.drawable.clear_button_background);
        clearButton.setAllCaps(false);
        clearButton.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 32, 0, 32);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        clearButton.setLayoutParams(params);

        notificationContainer.addView(clearButton);

        clearButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
            prefs.edit().remove("notification_history").apply();
            notificationContainer.removeAllViews();
            TextView emptyView = new TextView(this);
            emptyView.setText("No notifications yet.");
            emptyView.setTextSize(16);
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setGravity(Gravity.CENTER);
            notificationContainer.addView(emptyView);
        });
    }
}