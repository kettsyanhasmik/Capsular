package com.example.capsular.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userEdt, passEdt;
    private Button loginBtn, testUserLoginBtn;
    private TextView signupRedirectText, textView3;
    private CheckBox showPasswordCheckBox;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        userEdt = findViewById(R.id.userEdt);
        passEdt = findViewById(R.id.passEdt);
        loginBtn = findViewById(R.id.loginBtn);
        testUserLoginBtn = findViewById(R.id.testUserLoginBtn);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        textView3 = findViewById(R.id.textView3);
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        progressBar = findViewById(R.id.loginProgressBar);

        passEdt.setTransformationMethod(new CustomPasswordTransformationMethod());
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passEdt.setTransformationMethod(new CustomPasswordTransformationMethod());
            }
            passEdt.setSelection(passEdt.getText().length());
        });

        loginBtn.setOnClickListener(v -> performLogin());
        testUserLoginBtn.setOnClickListener(v -> loginAsTestUser());

        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        textView3.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void performLogin() {
        String email = userEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog("Email and Password cannot be empty");
            return;
        }

        showProgress(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserInfoAndGoToHome(user.getUid(), user.getEmail());
                        }
                    } else {
                        showErrorDialog("Login Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void loginAsTestUser() {
        String testEmail = "individualproject2025@gmail.com";
        String testPassword = "Samsung2025";

        showProgress(true);
        mAuth.signInWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Test User Login Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserInfoAndGoToHome(user.getUid(), testEmail);
                        }
                    } else {
                        showErrorDialog("Test Login Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserInfoAndGoToHome(String uid, String email) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_uid", uid);
        editor.putString("user_email", email);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showForgotPasswordDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Forgot Password?")
                .setMessage("Reset your password through the email linked to your account.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showProgress(boolean visible) {
        if (progressBar != null) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
            loginBtn.setEnabled(!visible);
            testUserLoginBtn.setEnabled(!visible);
        }
    }
}
