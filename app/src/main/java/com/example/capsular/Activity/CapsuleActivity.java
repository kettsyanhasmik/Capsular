package com.example.capsular.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.capsular.R;
import com.example.capsular.Receiver.CapsuleNotificationReceiver;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CapsuleActivity extends AppCompatActivity {

    private EditText headerEditText, messageEditText, emailEditText;
    private TextView datePickerText, headerCharCount, messageCharCount;
    private LinearLayout datePickerLayout, attachFileLayout, attachMediaLayout;
    private LinearLayout attachmentPreviewLayout, attachmentPreviewLayout2;

    private static final int REQUEST_FILE = 101;
    private static final int REQUEST_MEDIA = 102;
    private static final int NOTIFICATION_PERMISSION_CODE = 103;
    private static final String TAG = "CapsuleActivity";

    private final Calendar calendar = Calendar.getInstance();
    private final List<Uri> attachedFiles = new ArrayList<>();
    private final List<Uri> attachedMedia = new ArrayList<>();
    private boolean isNotificationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsule);

        headerEditText = findViewById(R.id.headerEditText);
        messageEditText = findViewById(R.id.messageEditText);
        headerCharCount = findViewById(R.id.headerCharCount);
        messageCharCount = findViewById(R.id.messageCharCount);
        datePickerText = findViewById(R.id.datePickerText);
        datePickerLayout = findViewById(R.id.datePickerLayout);
        attachFileLayout = findViewById(R.id.attachFileLayout);
        attachMediaLayout = findViewById(R.id.attachMediaLayout);
        attachmentPreviewLayout = findViewById(R.id.attachmentPreviewLayout);
        attachmentPreviewLayout2 = findViewById(R.id.attachmentPreviewLayout2);

        datePickerLayout.setOnClickListener(v -> openDatePicker());
        attachFileLayout.setOnClickListener(v -> openFilePicker());
        attachMediaLayout.setOnClickListener(v -> openMediaPicker());
        findViewById(R.id.createButton).setOnClickListener(v -> createCapsule());

        headerEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                headerCharCount.setText(s.length() + "/100");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        String presetTitle = getIntent().getStringExtra("preset_title");
        if (presetTitle != null) {
            headerEditText.setText(presetTitle);
            headerEditText.setSelection(presetTitle.length());
        }

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageCharCount.setText(s.length() + "/1000");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                isNotificationPermissionGranted = true;
            }
        } else {
            isNotificationPermissionGranted = true;
        }
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.TealDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    datePickerText.setText(formatter.format(calendar.getTime()));
                    datePickerText.setTextColor(Color.BLACK);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            isNotificationPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!isNotificationPermissionGranted) {
                Toast.makeText(this, "Notification permission denied. Please enable it in Settings to receive reminders.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_FILE);
    }

    private void openMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select Media"), REQUEST_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_FILE && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    attachedFiles.add(uri);
                    addFilePreview(uri);
                } catch (SecurityException e) {
                    Log.e(TAG, "Failed to access selected file", e);
                    Toast.makeText(this, "Failed to access selected file", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_MEDIA) {
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            attachedMedia.add(uri);
                            addMediaPreview(uri);
                        } catch (SecurityException e) {
                            Log.e(TAG, "Failed to access media item", e);
                            Toast.makeText(this, "Failed to access media item", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        attachedMedia.add(uri);
                        addMediaPreview(uri);
                    } catch (SecurityException e) {
                        Log.e(TAG, "Failed to access selected media", e);
                        Toast.makeText(this, "Failed to access selected media", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void scheduleCapsuleNotification(String dateString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date openDate = formatter.parse(dateString);
            if (openDate == null) {
                Log.e(TAG, "❌ Parsed date is null");
                return;
            }

            long timeInMillis = openDate.getTime();
            long currentTime = System.currentTimeMillis();

            Intent intent = new Intent(this, CapsuleNotificationReceiver.class);
            intent.putExtra("capsule_date", dateString);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    dateString.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Log.e(TAG, "❌ Cannot schedule exact alarms. Requesting permission...");
                        Intent intentPermission = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intentPermission);
                        return;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }

                Log.d(TAG, "✅ Notification scheduled for " + dateString + ", timeInMillis: " + timeInMillis);
            } else {
                Log.e(TAG, "❌ AlarmManager is null");
            }
        } catch (ParseException e) {
            Log.e(TAG, "❌ Error parsing date for scheduling", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Unexpected error: " + e.getMessage(), e);
        }
    }

    private void addFilePreview(Uri uri) {
        TextView fileView = new TextView(this);
        fileView.setText("\uD83D\uDCCE " + uri.getLastPathSegment());
        fileView.setTextColor(Color.BLACK);
        fileView.setPadding(12, 12, 12, 12);
        fileView.setBackgroundResource(R.drawable.edit_text_rounded);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        fileView.setLayoutParams(params);
        attachmentPreviewLayout.addView(fileView);
    }

    private void addMediaPreview(Uri uri) {
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(uri);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setBackgroundResource(R.drawable.edit_text_rounded);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
        params.setMargins(0, 8, 0, 8);
        imageView.setLayoutParams(params);
        attachmentPreviewLayout2.addView(imageView);
    }

    private void createCapsule() {
        String header = headerEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();
        String date = datePickerText.getText().toString().trim();

        if (header.isEmpty() || date.equals(getString(R.string.date_default))) {
            Toast.makeText(this, "Please fill in the header and select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("capsules", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String existingList = prefs.getString("capsule_list", "");
        if (!existingList.contains(date)) {
            if (!existingList.isEmpty()) {
                existingList += ",";
            }
            existingList += date;
            editor.putString("capsule_list", existingList);
        }

        editor.putString("capsule_header_" + date, header);
        editor.putString("capsule_message_" + date, message);
        editor.putBoolean("capsule_opened_" + date, false);
        editor.putBoolean("capsule_saved_" + date, false);

        if (!attachedMedia.isEmpty()) {
            List<String> mediaUris = new ArrayList<>();
            for (Uri uri : attachedMedia) {
                mediaUris.add(uri.toString());
            }
            editor.putString("capsule_media_" + date, String.join(",", mediaUris));
        }

        if (!attachedFiles.isEmpty()) {
            List<String> fileUris = new ArrayList<>();
            for (Uri uri : attachedFiles) {
                fileUris.add(uri.toString());
            }
            editor.putString("capsule_files_" + date, String.join(",", fileUris));
        }

        scheduleCapsuleNotification(date);

        editor.apply();
        Log.d(TAG, "✅ Capsule saved for " + date);

        Toast.makeText(this, "🎉 Capsule created!", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    private void sendInvitationEmail(String recipientEmail, String capsuleTitle, String capsuleDate, String deepLink) {
        Log.d("EmailSender", "sendInvitationEmail() called for: " + recipientEmail);
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("toEmail", recipientEmail);
                json.put("capsuleTitle", capsuleTitle);
                json.put("capsuleDate", capsuleDate);
                json.put("capsuleLink", deepLink);
                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                Request request = new Request.Builder().url("https://sendcapsuleemail-prehmbm3ha-uc.a.run.app").post(body).build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                Log.d("EmailSender", "Response code: " + response.code());
                Log.d("EmailSender", "Response body: " + responseBody);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this, "Invitation email sent!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to send email.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("EmailSender", "Exception: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Error sending email.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}