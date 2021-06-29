package com.example.klaf;

import android.os.Handler;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MyTimer {
    private final TextView textView;
    private int totalSeconds;
    private boolean run;
    private int savedTotalSeconds;
    private boolean paused;

    public MyTimer(TextView textView) {
        this.textView = textView;
        totalSeconds = 0;
        run = false;
        savedTotalSeconds = 0;
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void runCount() {
        run = true;
        paused = false;
        setColorByRun();
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.white));
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (run) {
                String time = getTimeAsString(totalSeconds);
                textView.setText(time);

                    totalSeconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    public String getTimeAsString(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void stopCount() {
        run = false;
        setColorByRun();
        savedTotalSeconds = totalSeconds;
        totalSeconds = 0;
        textView.setText("00:00");
    }

    public void pauseCounting() {
        run = false;
        paused = true;
        setColorByRun();
    }

    private void setColorByRun() {
        if (run) {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.white));
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.hint));
        }
    }

    public boolean isRun() {
        return run;
    }

    public int getSavedTotalSeconds() {
        return savedTotalSeconds;
    }
}
