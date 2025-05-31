package com.example.capsular.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.capsular.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText nameEditText;
    private ImageView confirmIcon;
    private Uri imageUri;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        profileImage = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.editTextName);
        confirmIcon = findViewById(R.id.confirmIcon);
        emailTextView = findViewById(R.id.emailTextView);
        ImageView homeIcon = findViewById(R.id.home_icon);
        TextView homeText = findViewById(R.id.home_text);
        ImageView profileIcon = findViewById(R.id.profile_icon);
        TextView profileText = findViewById(R.id.profile_text);

        profileIcon.setImageResource(R.drawable.image_user_filled);
        profileText.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));

        homeIcon.setImageResource(R.drawable.image_home);
        homeText.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));

        findViewById(R.id.home_button).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        });


        loadProfileData();

        profileImage.setOnClickListener(v -> openGallery());
        nameEditText.setOnClickListener(v -> enableNameEditing());
        confirmIcon.setOnClickListener(v -> saveName());

        ImageButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> navigateToActivity(CapsuleActivity.class));

        ConstraintLayout notificationsLayout = findViewById(R.id.notifications_layout);
        notificationsLayout.setOnClickListener(v -> navigateToActivity(NotificationSettingsActivity.class));

        ConstraintLayout accountSettingsLayout = findViewById(R.id.account_settings_layout);
        accountSettingsLayout.setOnClickListener(v -> navigateToActivity(AccountSettingsActivity.class));

        ConstraintLayout supportLayout = findViewById(R.id.contact_support_layout);
        supportLayout.setOnClickListener(v -> navigateToActivity(ContactSupportActivity.class));

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmIcon.setVisibility(View.VISIBLE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProfileData() {
        String savedName = sharedPreferences.getString("profile_name", "");
        nameEditText.setText(savedName);
        disableNameEditing();

        String savedImageUri = sharedPreferences.getString("profile_image", null);
        if (savedImageUri != null) {
            try {
                Glide.with(this)
                        .load(Uri.parse(savedImageUri))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .error(R.drawable.profileprofile)
                        .into(profileImage);
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.profileprofile);
            }
        } else {
            profileImage.setImageResource(R.drawable.profileprofile);
        }

        String savedEmail = sharedPreferences.getString("user_email", "your@email.com");
        emailTextView.setText(savedEmail);
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void enableNameEditing() {
        nameEditText.setFocusable(true);
        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        nameEditText.setCursorVisible(true);
        nameEditText.requestFocus();
        confirmIcon.setVisibility(View.VISIBLE);
        showKeyboard(nameEditText);
    }

    private void disableNameEditing() {
        nameEditText.setFocusable(false);
        nameEditText.setFocusableInTouchMode(false);
        nameEditText.setInputType(InputType.TYPE_NULL);
        nameEditText.setCursorVisible(false);
        confirmIcon.setVisibility(View.GONE);
        hideKeyboard(nameEditText);
    }

    private void saveName() {
        String newName = nameEditText.getText().toString().trim();
        if (!newName.isEmpty()) {
            editor.putString("profile_name", newName);
        } else {
            newName = "Name";
            editor.putString("profile_name", newName);
            nameEditText.setText(newName);
        }
        editor.apply();
        disableNameEditing();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            try {
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                editor.putString("profile_image", imageUri.toString());
                editor.apply();
                Glide.with(this)
                        .load(imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .error(R.drawable.profileprofile)
                        .into(profileImage);
            } catch (SecurityException e) {
                profileImage.setImageResource(R.drawable.profileprofile);
            }
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isNameEditing", nameEditText.isFocusable());
        if (imageUri != null) {
            outState.putString("imageUri", imageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("isNameEditing", false)) {
            enableNameEditing();
        }
        String savedImageUri = savedInstanceState.getString("imageUri");
        if (savedImageUri != null) {
            imageUri = Uri.parse(savedImageUri);
            Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .error(R.drawable.profileprofile)
                    .into(profileImage);
        }
    }
}
