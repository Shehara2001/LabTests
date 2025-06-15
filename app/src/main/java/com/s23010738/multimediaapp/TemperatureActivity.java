package com.s23010738.multimediaapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TemperatureActivity extends AppCompatActivity implements SensorEventListener {

    private TextView      tvTemp;
    private SensorManager sensorManager;
    private Sensor        tempSensor;

    private MediaPlayer   mediaPlayer;
    private boolean       alarmPlaying = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.tempv2);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        tvTemp        = findViewById(R.id.textView10);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        if (sensorManager != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }
        if (tempSensor == null) {
            Toast.makeText(this, "No ambient‑temperature sensor found on this device",
                    Toast.LENGTH_LONG).show();
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.audio);
        mediaPlayer.setOnCompletionListener(mp -> alarmPlaying = false);
    }


    @Override public void onSensorChanged(SensorEvent event) {
        float celsius = event.values[0];

        tvTemp.setText(String.format(Locale.US, "%.1f °C", celsius));


        if (celsius > 38f && !alarmPlaying) {
            alarmPlaying = true;
            mediaPlayer.start();
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


    @Override protected void onResume() {
        super.onResume();
        if (tempSensor != null) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override protected void onDestroy() {                   // ★ release resources
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
