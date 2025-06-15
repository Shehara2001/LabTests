package com.s23010738.multimediaapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.background1);
        videoView.start();
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        EditText usernameField = findViewById(R.id.editTextText);
        EditText passwordField = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                boolean inserted = dbHelper.insertData(username, password);
                if (inserted) {
                    Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }

                // Optional: Navigate to MapActivity
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
