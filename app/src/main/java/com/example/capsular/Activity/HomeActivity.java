package com.example.capsular.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.capsular.R;
import com.example.capsular.Receiver.CapsuleNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int CAPSULE_CREATE_REQUEST_CODE = 101;

    private LinearLayout capsuleRow;
    private ConstraintLayout profileButton;
    private LinearLayout homeButton, viewSavedButton;
    public de.hdodenhof.circleimageview.CircleImageView profileImage;
    private TextView profileNameText;
    private HorizontalScrollView capsuleScrollView;
    private TextView capsuleTextTop, capsuleTextMiddle1, capsuleTextMiddle2, capsuleTextBottom;
    private View notificationDot;
    private View currentlySelectedCapsule = null;
    private String selectedCapsuleDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        capsuleRow = findViewById(R.id.capsule_row);
        profileButton = findViewById(R.id.profile_button);
        homeButton = findViewById(R.id.home_button);
        viewSavedButton = findViewById(R.id.view_saved_button);
        profileImage = findViewById(R.id.home_profile_image);
        profileNameText = findViewById(R.id.textView);
        capsuleScrollView = findViewById(R.id.capsule_scroll_view);
        notificationDot = findViewById(R.id.notification_dot);
        capsuleTextTop = findViewById(R.id.capsule_text_top);
        capsuleTextMiddle1 = findViewById(R.id.capsule_text_middle_1);
        capsuleTextMiddle2 = findViewById(R.id.capsule_text_middle_2);
        capsuleTextBottom = findViewById(R.id.capsule_text_bottom);
        ImageView homeIcon = findViewById(R.id.home_icon);
        TextView homeText = findViewById(R.id.home_text);
        ImageView profileIcon = findViewById(R.id.profile_icon);
        TextView profileText = findViewById(R.id.profile_text);

        homeIcon.setImageResource(R.drawable.image_home_filled);
        homeText.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));

        profileIcon.setImageResource(R.drawable.image_user);
        profileText.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));

        findViewById(R.id.home_button).setOnClickListener(v -> {
            homeIcon.setImageResource(R.drawable.image_home_filled);
            homeText.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));

            profileIcon.setImageResource(R.drawable.image_user);
            profileText.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));
        });

        findViewById(R.id.profile_button_nav).setOnClickListener(v -> {
            profileIcon.setImageResource(R.drawable.image_user_filled);
            profileText.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));

            homeIcon.setImageResource(R.drawable.image_home);
            homeText.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));
        });

        findViewById(R.id.idea_birthday).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Birthday Wishes 🎂");
            startActivity(intent);
        });

        findViewById(R.id.idea_event).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Event Highlights 🎉");
            startActivity(intent);
        });

        findViewById(R.id.idea_dreams).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "My Dreams & Goals 🎯");
            startActivity(intent);
        });

        findViewById(R.id.idea_message).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Message for My Future ✉️");
            startActivity(intent);
        });

        findViewById(R.id.idea_travel).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "My Travel Goals 🌍");
            startActivity(intent);
        });

        findViewById(R.id.idea_memory).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Funny Memories 😂");
            startActivity(intent);
        });

        findViewById(R.id.idea_love).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Love Letter ❤️");
            startActivity(intent);
        });

        findViewById(R.id.idea_secret).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Secret Capsule 🤫");
            startActivity(intent);
        });

        findViewById(R.id.idea_quote).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Favorite Quotes 📖");
            startActivity(intent);
        });

        findViewById(R.id.idea_grateful).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "What I’m Grateful For 🙏");
            startActivity(intent);
        });

        findViewById(R.id.idea_career).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Career Dreams 💼");
            startActivity(intent);
        });

        findViewById(R.id.idea_challenge).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Challenge Myself 🥊");
            startActivity(intent);
        });

        findViewById(R.id.idea_achievements).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "My Achievements 🏆");
            startActivity(intent);
        });
        
        findViewById(R.id.idea_learned).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Today I Learned 🧠");
            startActivity(intent);
        });

        findViewById(R.id.idea_random_thought).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "Random Thought 💭");
            startActivity(intent);
        });

        findViewById(R.id.idea_one_story).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "One Photo, One Story 📸");
            startActivity(intent);
        });

        findViewById(R.id.idea_predictions).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CapsuleActivity.class);
            intent.putExtra("preset_title", "My Predictions 🔮");
            startActivity(intent);
        });

        setupListeners();
        loadProfileData();
        loadCapsules();
        updateNotificationDot();

        findViewById(R.id.capsule_container).setOnClickListener(v -> {
            if (selectedCapsuleDate != null) {
                try {
                    Date openDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(selectedCapsuleDate);
                    Date now = new Date();
                    if (!openDate.after(now)) {
                        Intent intent = new Intent(HomeActivity.this, CapsuleViewerActivity.class);
                        intent.putExtra("capsule_date", selectedCapsuleDate);
                        startActivity(intent);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing capsule date for opening", e);
                }
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.plus_icon_navbar).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, CapsuleActivity.class), CAPSULE_CREATE_REQUEST_CODE);
        });

        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        profileImage.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.profile_button_nav).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        viewSavedButton.setOnClickListener(v -> startActivity(new Intent(this, SavedCapsuleActivity.class)));
        findViewById(R.id.relativeLayout).setOnClickListener(v -> startActivity(new Intent(this, NotificationHistoryActivity.class)));
        findViewById(R.id.plus_button).setOnClickListener(v -> startActivity(new Intent(this, CapsuleActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
        loadCapsules();
        updateNotificationDot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPSULE_CREATE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadCapsules();
        }
    }

    private void loadCapsules() {
        new Thread(() -> {
            SharedPreferences prefs = getSharedPreferences("capsules", MODE_PRIVATE);
            String capsuleList = prefs.getString("capsule_list", "");

            runOnUiThread(() -> {
                View plusButton = findViewById(R.id.plus_button);

                capsuleRow.removeAllViews();

                if (plusButton != null) {
                    if (plusButton.getParent() instanceof LinearLayout) {
                        ((LinearLayout) plusButton.getParent()).removeView(plusButton);
                    }
                    capsuleRow.addView(plusButton);
                }

                List<String> updatedCapsuleList = new ArrayList<>();
                View firstCapsuleView = null;

                if (!capsuleList.isEmpty()) {
                    List<String> capsules = new ArrayList<>(Arrays.asList(capsuleList.split(",")));
                    for (String date : capsules) {
                        if (!date.trim().isEmpty()) {
                            boolean isOpened = prefs.getBoolean("capsule_opened_" + date, false);
                            boolean isSaved = prefs.getBoolean("capsule_saved_" + date, false);
                            if (isOpened && !isSaved) {
                                deleteCapsule(date);
                                continue;
                            }

                            updatedCapsuleList.add(date);
                            View capsuleView = createCapsuleView(date);
                            capsuleRow.addView(capsuleView);

                            if (firstCapsuleView == null) firstCapsuleView = capsuleView;
                        }
                    }

                    prefs.edit().putString("capsule_list", String.join(",", updatedCapsuleList)).apply();

                    if (firstCapsuleView != null) {
                        firstCapsuleView.performClick();
                    } else {
                        showEmptyCapsuleMessage();
                    }
                } else {
                    showEmptyCapsuleMessage();
                }
            });
        }).start();
    }

    private void deleteCapsule(String date) {
        Intent intent = new Intent(this, CapsuleNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                date.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                Log.d(TAG, "✅ Cancelled notification for capsule: " + date);
            }
        }

        SharedPreferences prefs = getSharedPreferences("capsules", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("capsule_header_" + date);
        editor.remove("capsule_message_" + date);
        editor.remove("capsule_opened_" + date);
        editor.remove("capsule_saved_" + date);
        editor.remove("capsule_files_" + date);
        editor.remove("capsule_media_" + date);

        String existing = prefs.getString("capsule_list", "");
        if (!existing.isEmpty()) {
            StringBuilder updated = new StringBuilder();
            for (String d : existing.split(",")) {
                if (!d.equals(date)) {
                    if (updated.length() > 0) updated.append(",");
                    updated.append(d);
                }
            }
            editor.putString("capsule_list", updated.toString());
        }

        editor.apply();
        Log.d(TAG, "✅ Deleted capsule: " + date);
    }

    private void updateNotificationDot() {
        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        boolean hasUnread = prefs.getBoolean("hasUnread", false);
        notificationDot.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        profileNameText.setText(prefs.getString("profile_name", "Name"));
        String imageUri = prefs.getString("profile_image", null);
        if (imageUri != null) {
            try {
                profileImage.setImageURI(Uri.parse(imageUri));
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile image: " + e.getMessage(), e);
            }
        }
    }

    private View createCapsuleView(String date) {
        View capsuleView = getLayoutInflater().inflate(R.layout.item_capsule, capsuleRow, false);
        try {
            Date parsedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(date);
            ((TextView) capsuleView.findViewById(R.id.capsule_weekday)).setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(parsedDate));
            ((TextView) capsuleView.findViewById(R.id.capsule_day_month)).setText(new SimpleDateFormat("dd MMM", Locale.getDefault()).format(parsedDate));
            ((TextView) capsuleView.findViewById(R.id.capsule_year)).setText(new SimpleDateFormat("yyyy", Locale.getDefault()).format(parsedDate));

            capsuleView.setOnClickListener(v -> {
                if (currentlySelectedCapsule != null) {
                    currentlySelectedCapsule.setSelected(false);
                    setTextColorForCapsule(currentlySelectedCapsule, false);
                }
                capsuleView.setSelected(true);
                setTextColorForCapsule(capsuleView, true);
                currentlySelectedCapsule = capsuleView;
                updateBigCapsule(date);
            });

            capsuleView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Capsule")
                        .setMessage("Are you sure you want to delete this capsule?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteCapsule(date))
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing capsule date: " + e.getMessage(), e);
        }

        return capsuleView;
    }

    private void updateBigCapsule(String capsuleDate) {
        try {
            Date openDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(capsuleDate);
            long daysRemaining = (openDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
            selectedCapsuleDate = capsuleDate;
            capsuleTextTop.setVisibility(View.GONE);

            if (daysRemaining > 0) {
                capsuleTextMiddle1.setText(daysRemaining + " Days Left");
                capsuleTextMiddle2.setText("Until Opening");
                capsuleTextBottom.setText("Your capsule opens on " + capsuleDate + " 🎉");
            } else if (daysRemaining == 0) {
                capsuleTextMiddle1.setText("Opens");
                capsuleTextMiddle2.setText("Today! 🎊");
                capsuleTextBottom.setText("Tap to open!");
            } else {
                capsuleTextMiddle1.setText("Opened");
                capsuleTextMiddle2.setText(Math.abs(daysRemaining) + " days ago");
                capsuleTextBottom.setText("You can open it now!");
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error updating big capsule: " + e.getMessage(), e);
        }
    }

    private void showEmptyCapsuleMessage() {
        capsuleTextTop.setVisibility(View.VISIBLE);
        capsuleTextTop.setText("No Capsules Yet?");
        capsuleTextMiddle1.setText("Create Your");
        capsuleTextMiddle2.setText("First One!");
        capsuleTextBottom.setText("A memory worth waiting for! ✨");
        selectedCapsuleDate = null;
    }

    private void setTextColorForCapsule(View capsuleView, boolean selected) {
        int color = selected ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black);
        TextView weekday = capsuleView.findViewById(R.id.capsule_weekday);
        TextView dayMonth = capsuleView.findViewById(R.id.capsule_day_month);
        TextView year = capsuleView.findViewById(R.id.capsule_year);
        if (weekday != null) weekday.setTextColor(color);
        if (dayMonth != null) dayMonth.setTextColor(color);
        if (year != null) year.setTextColor(color);
    }
}