package com.samyaktechlabs.notificationtestapplication;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText tokenEditText;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted
                        Toast.makeText(MainActivity.this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission is denied
                        Toast.makeText(MainActivity.this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Request notification permission if on Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
            }
        }


        tokenEditText = findViewById(R.id.tokenEditText);
        Button copyButton = findViewById(R.id.copyButton);

        // Set contrasting colors for light and dark theme
        setEditTextColors();
        // Fetch the token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                tokenEditText.setText(token);
            } else {
                Toast.makeText(MainActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
            }
        });

        // Copy to clipboard
        copyButton.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(tokenEditText.getText().toString());
            Toast.makeText(MainActivity.this, "Token copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void setEditTextColors() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme: light background with dark text
            tokenEditText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            tokenEditText.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        } else {
            // Light theme: dark background with light text
            tokenEditText.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            tokenEditText.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    private void requestNotificationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }
}