package com.example.capsular.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;

public class EnterVerificationCodeActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button verifyBtn;
    private String expectedCode;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_verification_code);

        codeInput = findViewById(R.id.codeInput);
        verifyBtn = findViewById(R.id.verifyBtn);

        expectedCode = getIntent().getStringExtra("verificationCode");
        userEmail = getIntent().getStringExtra("userEmail");

        verifyBtn.setOnClickListener(v -> {
            String enteredCode = codeInput.getText().toString().trim();

            if (enteredCode.equals(expectedCode)) {
                Toast.makeText(this, "✅ Verification successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "❌ Incorrect code. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
