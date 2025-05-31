package com.example.capsular.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.capsular.R;

import java.util.Arrays;
import java.util.List;

public class CapsuleViewerActivity extends AppCompatActivity {

    private TextView headerText, messageText, openDateText;
    private LinearLayout imagePreviewLayout, filePreviewLayout;
    private Button saveButton, dismissButton;
    private ImageView shareIcon;
    private String capsuleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capsule_viewer);

        headerText = findViewById(R.id.headerText);
        messageText = findViewById(R.id.messageText);
        openDateText = findViewById(R.id.openDateText);
        imagePreviewLayout = findViewById(R.id.imagePreviewLayout);
        filePreviewLayout = findViewById(R.id.filePreviewLayout);
        saveButton = findViewById(R.id.saveButton);
        dismissButton = findViewById(R.id.dismissButton);
        shareIcon = findViewById(R.id.shareIcon);

        capsuleDate = null;
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null && data.getPathSegments().size() > 1) {
            capsuleDate = data.getLastPathSegment();
            Log.d("DeepLink", "Loaded capsule from link with ID: " + capsuleDate);
        } else {
            capsuleDate = intent.getStringExtra("capsule_date");
        }

        if (capsuleDate == null) {
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("capsules", MODE_PRIVATE);

        boolean alreadyOpened = prefs.getBoolean("capsule_opened_" + capsuleDate, false);
        if (!alreadyOpened) {
            prefs.edit()
                    .putBoolean("capsule_opened_" + capsuleDate, true)
                    .putBoolean("capsule_saved_" + capsuleDate, true)
                    .apply();
        }

        String header = prefs.getString("capsule_header_" + capsuleDate, null);
        String message = prefs.getString("capsule_message_" + capsuleDate, null);
        if (header == null || message == null) {
            Toast.makeText(this, "Capsule data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        headerText.setText(header);
        messageText.setText(message);
        openDateText.setText("Opened on: " + capsuleDate);
        loadAttachments(capsuleDate);

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("capsule_opened_" + capsuleDate, true)
                    .putBoolean("capsule_saved_" + capsuleDate, true);

            String savedList = prefs.getString("saved_capsule_list", "");
            if (!savedList.contains(capsuleDate)) {
                savedList = savedList.isEmpty() ? capsuleDate : savedList + "," + capsuleDate;
                editor.putString("saved_capsule_list", savedList);
            }

            String list = prefs.getString("capsule_list", "");
            if (list != null && !list.isEmpty()) {
                StringBuilder updatedList = new StringBuilder();
                for (String d : list.split(",")) {
                    if (!d.equals(capsuleDate)) {
                        if (updatedList.length() > 0) updatedList.append(",");
                        updatedList.append(d);
                    }
                }
                editor.putString("capsule_list", updatedList.toString());
            }

            editor.apply();
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        dismissButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("capsule_header_" + capsuleDate);
            editor.remove("capsule_message_" + capsuleDate);
            editor.remove("capsule_opened_" + capsuleDate);
            editor.remove("capsule_saved_" + capsuleDate);
            editor.remove("capsule_media_" + capsuleDate);
            editor.remove("capsule_files_" + capsuleDate);

            String list = prefs.getString("capsule_list", "");
            if (list != null && !list.isEmpty()) {
                StringBuilder updatedList = new StringBuilder();
                for (String d : list.split(",")) {
                    if (!d.equals(capsuleDate)) {
                        if (updatedList.length() > 0) updatedList.append(",");
                        updatedList.append(d);
                    }
                }
                editor.putString("capsule_list", updatedList.toString());
            }

            editor.apply();
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        shareIcon.setOnClickListener(v -> {
            String shareLink = "https://capsular.app/capsule/" + capsuleDate;
            String messageToShare = "\uD83D\uDC8C I just opened a memory capsule on Capsular!\n\n" +
                    "\uD83D\uDCCB Opened on: " + capsuleDate + "\n" +
                    "\uD83D\uDCDD Message: " + messageText.getText().toString() + "\n\n" +
                    "\uD83D\uDC49 Open it here: " + shareLink + "\n\n" +
                    "\u2728 Download Capsular and try it yourself!";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, messageToShare);
            startActivity(Intent.createChooser(shareIntent, "Share Capsule via"));
        });
    }

    private void loadAttachments(String date) {
        SharedPreferences prefs = getSharedPreferences("capsules", MODE_PRIVATE);

        String fileStr = prefs.getString("capsule_files_" + date, "");
        String mediaStr = prefs.getString("capsule_media_" + date, "");

        if (!fileStr.isEmpty()) {
            List<String> files = Arrays.asList(fileStr.split(","));
            for (String fileUri : files) {
                Uri uri = Uri.parse(fileUri);
                TextView fileView = new TextView(this);
                fileView.setText("\uD83D\uDCCE " + uri.getLastPathSegment());
                fileView.setTextSize(16);
                fileView.setPadding(12, 12, 12, 12);
                fileView.setBackgroundResource(R.drawable.edit_text_rounded);
                fileView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "*/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                });
                filePreviewLayout.addView(fileView);
            }
        }

        if (!mediaStr.isEmpty()) {
            List<String> media = Arrays.asList(mediaStr.split(","));
            for (String mediaUri : media) {
                Uri uri = Uri.parse(mediaUri);
                String type = getContentResolver().getType(uri);

                if (type != null && type.startsWith("image")) {
                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 400);
                    params.setMargins(0, 16, 0, 16);
                    img.setLayoutParams(params);
                    img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    img.setAdjustViewBounds(true);
                    img.setBackgroundResource(R.drawable.image_background_rounded);

                    Glide.with(this)
                            .load(uri)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(img);

                    img.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    });

                    imagePreviewLayout.addView(img);

                } else if (type != null && type.startsWith("video")) {
                    VideoView videoView = new VideoView(this);
                    videoView.setVideoURI(uri);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 400);
                    params.setMargins(0, 16, 0, 16);
                    videoView.setLayoutParams(params);
                    videoView.setBackgroundResource(R.drawable.image_background_rounded);
                    videoView.setMediaController(new android.widget.MediaController(this));
                    videoView.seekTo(100);

                    videoView.setOnClickListener(v -> {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else {
                            videoView.start();
                        }
                    });

                    imagePreviewLayout.addView(videoView);

                } else if (type != null && type.startsWith("audio")) {
                    Button playButton = new Button(this);
                    playButton.setText("\u25B6\uFE0F Play Audio");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 16, 0, 16);
                    playButton.setLayoutParams(params);

                    MediaPlayer[] player = {null};

                    playButton.setOnClickListener(v -> {
                        if (player[0] == null) {
                            player[0] = new MediaPlayer();
                            try {
                                player[0].setDataSource(this, uri);
                                player[0].prepare();
                                player[0].start();
                                playButton.setText("\u23F8 Pause Audio");

                                player[0].setOnCompletionListener(mp -> {
                                    playButton.setText("\u25B6\uFE0F Play Audio");
                                    player[0].release();
                                    player[0] = null;
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
                            }

                        } else if (player[0].isPlaying()) {
                            player[0].pause();
                            playButton.setText("\u25B6\uFE0F Play Audio");
                        } else {
                            player[0].start();
                            playButton.setText("\u23F8 Pause Audio");
                        }
                    });

                    imagePreviewLayout.addView(playButton);

                } else {
                    TextView unknownView = new TextView(this);
                    unknownView.setText("\uD83D\uDCC1 Unknown file: " + uri.getLastPathSegment());
                    unknownView.setPadding(12, 12, 12, 12);
                    unknownView.setBackgroundResource(R.drawable.edit_text_rounded);
                    imagePreviewLayout.addView(unknownView);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
