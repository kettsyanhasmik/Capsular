package com.example.capsular.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;
import com.example.capsular.SupportMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactSupportActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, messageInput;
    private Button sendButton;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());


        nameInput = findViewById(R.id.input_name);
        emailInput = findViewById(R.id.input_email);
        messageInput = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.button_send);

        databaseRef = FirebaseDatabase.getInstance().getReference("support_messages");

        Toast.makeText(this, "ContactSupportActivity Loaded", Toast.LENGTH_SHORT).show();

        sendButton.setOnClickListener(v -> {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();

            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                saveMessage(name, email, message);
            }
        });
    }

    private void saveMessage(String name, String email, String message) {
        String id = databaseRef.push().getKey();
        SupportMessage supportMessage = new SupportMessage(id, name, email, message);

        databaseRef.child(id).setValue(supportMessage)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ContactSupportActivity.this, "Message sent to admin!", Toast.LENGTH_SHORT).show();
                    nameInput.setText("");
                    emailInput.setText("");
                    messageInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContactSupportActivity.this, "Failed to send: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
