package com.example.capsular.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;
import com.example.capsular.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.passEdt.setTransformationMethod(new CustomPasswordTransformationMethod());
        binding.confirmPassEdt.setTransformationMethod(new CustomPasswordTransformationMethod());

        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.passEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.confirmPassEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.passEdt.setTransformationMethod(new CustomPasswordTransformationMethod());
                binding.confirmPassEdt.setTransformationMethod(new CustomPasswordTransformationMethod());
            }
            binding.passEdt.setSelection(binding.passEdt.getText().length());
            binding.confirmPassEdt.setSelection(binding.confirmPassEdt.getText().length());
        });

        setSignupListener();
        setLoginRedirectListener();
    }

    private void setSignupListener() {
        binding.signupBtn.setOnClickListener(view -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();
            String confirmPassword = binding.confirmPassEdt.getText().toString().trim();

            if (email.isEmpty()) {
                showError("Email cannot be empty");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Enter a valid email address");
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                showError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                sendVerificationCode(user.getEmail());
                            }
                        } else {
                            showError("Signup failed: " + task.getException().getMessage());
                        }
                    });
        });
    }

    private void sendVerificationCode(String userEmail) {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        generatedCode = String.valueOf(code);

        new Thread(() -> {
            try {
                MailSender.sendEmail(userEmail, "Your Verification Code", "Your code is: " + generatedCode);

                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "Verification code sent to email.", Toast.LENGTH_LONG).show();
                    openVerificationScreen(userEmail, generatedCode);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> showError("Failed to send email: " + e.getMessage()));
            }
        }).start();
    }

    private void openVerificationScreen(String email, String code) {
        Intent intent = new Intent(SignupActivity.this, EnterVerificationCodeActivity.class);
        intent.putExtra("userEmail", email);
        intent.putExtra("verificationCode", code);
        startActivity(intent);
        finish();
    }

    private void setLoginRedirectListener() {
        binding.textView3.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void showError(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
