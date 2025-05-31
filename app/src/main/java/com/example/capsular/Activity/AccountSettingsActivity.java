package com.example.capsular.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;

public class AccountSettingsActivity extends AppCompatActivity {

    private LinearLayout changeEmailOption, deleteAccountOption, signOutOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        changeEmailOption = findViewById(R.id.option_change_email);
        deleteAccountOption = findViewById(R.id.option_delete_account);
        signOutOption = findViewById(R.id.option_sign_out);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        changeEmailOption.setOnClickListener(v -> showChangeEmailDialog());

        deleteAccountOption.setOnClickListener(v -> showDeleteConfirmation());

        signOutOption.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            prefs.edit().clear().apply();

            SharedPreferences settingsPrefs = getSharedPreferences("settings", MODE_PRIVATE);
            settingsPrefs.edit().clear().apply();

            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Email");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Enter new email");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (!newEmail.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
                prefs.edit().putString("user_email", newEmail).apply();
                Toast.makeText(this, "Email updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete your account? This will remove all your capsules and profile data.");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            getSharedPreferences("settings", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("capsules", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("notifications", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
