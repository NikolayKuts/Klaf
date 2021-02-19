package com.example.klaf.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.util.Log;

import com.example.klaf.data.KlafDatabase;
import com.example.klaf.notifications.NotificationAssembler;
import com.example.klaf.pojo.Desk;

import java.util.ArrayList;
import java.util.List;

public class RepetitionDayUpdater extends JobService {
    private static final int LATENCY = 43_200_000;
    private KlafDatabase database;
    private List<Desk> desks;

    @Override
    public void onCreate() {
//        super.onCreate();
        database = KlafDatabase.getInstance(this);
        desks = new ArrayList<>();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
            long currentTime = System.currentTimeMillis();
            desks.addAll(database.deskDao().getDeckList());
            updateDeskListByRepetitionDay(desks, currentTime);
            database.deskDao().insertDeskList(desks);

            NotificationAssembler notification = new NotificationAssembler(getApplicationContext());
            notification.setNotification("repetition days have been updated", "קלף");
            notification.showNotification();

            ComponentName componentName = new ComponentName(getApplicationContext(), RepetitionDayUpdater.class);
            int id = (int) currentTime;
            JobInfo.Builder infoBuilder = new JobInfo.Builder(id, componentName);
            infoBuilder.setMinimumLatency(LATENCY)
                    .setOverrideDeadline(LATENCY)
                    .setPersisted(true);
            JobInfo jobInfo = infoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

            jobFinished(params, false);
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void updateDeskListByRepetitionDay(List<Desk> desks, long currentTime) {

        for (Desk desk : desks) {
            long daysInMilliseconds = currentTime - desk.getCreationDate();
            int days = (int) daysInMilliseconds / 86_400_000;
            Log.i("log", "Desk: ");
            Log.i("log", "days  " + days);
            if (desk.getRepetitionDay() < days) {
                desk.setRepetitionDay(days);
            }
        }
    }
}
