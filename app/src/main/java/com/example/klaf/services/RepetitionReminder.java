package com.example.klaf.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.example.klaf.notifications.NotificationAssembler;
import com.example.klaf.screens.MainActivity;

public class RepetitionReminder extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
            String deskName = params.getExtras().getString(MainActivity.TAG_DECK_NAME);
            NotificationAssembler notification = new NotificationAssembler(getApplicationContext());
            notification.setNotification("It's time to repeat the desk \""
                    + deskName
                    + "\"", "קלף");
            notification.showNotification((int) System.currentTimeMillis());

            jobFinished(params, false);
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
