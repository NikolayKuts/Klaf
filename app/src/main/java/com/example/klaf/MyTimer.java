package com.example.klaf;

import android.os.Handler;
import android.widget.TextView;

import java.util.Locale;

public class MyTimer {
    private final TextView textView;
    private int totalSeconds;
    private boolean allowed;
    private int savedTotalSeconds;

    public MyTimer(TextView textView) {
        this.textView = textView;
        totalSeconds = 0;
        allowed = false;
        savedTotalSeconds = 0;
    }

    public void runCount() {
        allowed = true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (allowed) {
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%d", minutes, seconds);
                textView.setText(time);

                    totalSeconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    public void stopCount(){
        allowed = false;
        savedTotalSeconds = totalSeconds;
        totalSeconds = 0;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getSavedTotalSeconds() {
        return savedTotalSeconds;
    }
}
