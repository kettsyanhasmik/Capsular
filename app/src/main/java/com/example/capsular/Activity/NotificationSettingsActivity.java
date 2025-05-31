package com.example.capsular.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.capsular.R;

public class NotificationSettingsActivity extends AppCompatActivity {

    private SwitchCompat dailyReminderSwitch;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "settings";
    private static final String KEY_ENABLE_REMINDERS = "enable_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        dailyReminderSwitch = findViewById(R.id.switch_daily_reminder);
        ImageButton backButton = findViewById(R.id.backButton);

        if (dailyReminderSwitch == null || backButton == null) {
            finish();
            return;
        }

        boolean isEnabled = prefs.getBoolean(KEY_ENABLE_REMINDERS, true);
        dailyReminderSwitch.setChecked(isEnabled);

        dailyReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_ENABLE_REMINDERS, isChecked).apply();
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dailyReminderSwitch != null) {
            outState.putBoolean(KEY_ENABLE_REMINDERS, dailyReminderSwitch.isChecked());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && dailyReminderSwitch != null) {
            boolean isChecked = savedInstanceState.getBoolean(KEY_ENABLE_REMINDERS, prefs.getBoolean(KEY_ENABLE_REMINDERS, true));
            dailyReminderSwitch.setChecked(isChecked);
        }
    }
}