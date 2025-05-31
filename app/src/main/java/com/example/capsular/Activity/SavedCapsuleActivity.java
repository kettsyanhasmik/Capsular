package com.example.capsular.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capsular.R;

public class SavedCapsuleActivity extends AppCompatActivity {

    private LinearLayout savedCapsuleList;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_capsules);

        savedCapsuleList = findViewById(R.id.saved_capsule_list);
        emptyMessage = findViewById(R.id.emptyMessage);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> onBackPressed());

        SharedPreferences sharedPreferences = getSharedPreferences("capsules", MODE_PRIVATE);
        String savedList = sharedPreferences.getString("saved_capsule_list", "");

        if (!savedList.isEmpty()) {
            String[] dates = savedList.split(",");
            boolean foundAny = false;

            for (String date : dates) {
                boolean isOpened = sharedPreferences.getBoolean("capsule_opened_" + date, false);
                boolean isSaved = sharedPreferences.getBoolean("capsule_saved_" + date, false);

                if (isOpened && isSaved) {
                    String header = sharedPreferences.getString("capsule_header_" + date, "No Title");
                    String message = sharedPreferences.getString("capsule_message_" + date, "No Message");

                    View capsuleCard = LayoutInflater.from(this).inflate(R.layout.item_saved_capsule_card, savedCapsuleList, false);
                    TextView headerText = capsuleCard.findViewById(R.id.cardHeader);
                    TextView messageText = capsuleCard.findViewById(R.id.cardMessage);

                    headerText.setText("📦 " + header);
                    messageText.setText(message);

                    savedCapsuleList.addView(capsuleCard);
                    foundAny = true;
                }
            }

            if (!foundAny) {
                emptyMessage.setVisibility(View.VISIBLE);
            }

        } else {
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
