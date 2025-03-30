package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime;
    private Button btnStart, btnStop, btnReset ,btnContinue;
    private Handler handler;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private Runnable updateTimerThread;

    // MainActivity.java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnContinue = findViewById(R.id.btnContinue);

        handler = new Handler();

        // Continue Button Click Event
        btnContinue.setOnClickListener(v -> {
            startTime = SystemClock.uptimeMillis();
            handler.post(updateTimerThread);
            updateButtonStates(false, true, true, true);
        });

        // Runnable to update the stopwatch time
        updateTimerThread = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds;
                updateDisplay();
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        };

        // Start Button Click Event
        btnStart.setOnClickListener(v -> {
            startTime = SystemClock.uptimeMillis();
            handler.post(updateTimerThread);
            updateButtonStates(false, true, true,true);
        });

        // Stop Button Click Event
        btnStop.setOnClickListener(v -> {
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateTimerThread);
            updateButtonStates(true, false, true,true);
        });

        // Reset Button Click Event
        btnReset.setOnClickListener(v -> {
            startTime = timeSwapBuff = timeInMilliseconds = updateTime = 0L;
            updateDisplay();
            handler.removeCallbacks(updateTimerThread);
            updateButtonStates(true, false, false,true);
        });

//if the user rotate for the screen
        if (savedInstanceState != null) {
            startTime = savedInstanceState.getLong("startTime");
            timeInMilliseconds = savedInstanceState.getLong("timeInMilliseconds");
            timeSwapBuff = savedInstanceState.getLong("timeSwapBuff");
            updateTime = savedInstanceState.getLong("updateTime");
            boolean isRunning = savedInstanceState.getBoolean("isRunning");

            if (isRunning) {
                handler.post(updateTimerThread);
                updateButtonStates(false, true, true,true);
            } else {
                updateDisplay();
                updateButtonStates(true, false, true,true);
            }
        }
    }

    //save the state at make the screen rotate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
        outState.putLong("timeInMilliseconds", timeInMilliseconds);
        outState.putLong("timeSwapBuff", timeSwapBuff);
        outState.putLong("updateTime", updateTime);
        outState.putBoolean("isRunning", handler.hasCallbacks(updateTimerThread));
    }

    //restore the state at make the screen rotate
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startTime = savedInstanceState.getLong("startTime");
        timeInMilliseconds = savedInstanceState.getLong("timeInMilliseconds");
        timeSwapBuff = savedInstanceState.getLong("timeSwapBuff");
        updateTime = savedInstanceState.getLong("updateTime");
        boolean isRunning = savedInstanceState.getBoolean("isRunning");

        if (isRunning) {
            handler.post(updateTimerThread);
            updateButtonStates(false, true, true,true);
        } else {
            updateDisplay();
            updateButtonStates(true, false, true,true);
        }
    }

    // Helper method to update the displayed time
    private void updateDisplay() {
        int secs = (int) (updateTime / 1000);
        int mins = secs / 60;
        secs %= 60;
        tvTime.setText(String.format("%02d:%02d", mins, secs));
    }

    // Helper method to enable/disable buttons
    private void updateButtonStates(boolean start, boolean stop, boolean reset, boolean contunue ) {
        btnStart.setEnabled(start);
        btnStop.setEnabled(stop);
        btnReset.setEnabled(reset);
        btnContinue.setEnabled(contunue);
    }
}