package com.example.klaf.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;

import com.example.klaf.DateWorker;
import com.example.klaf.data.KlafDatabase;
import com.example.klaf.notifications.NotificationAssembler;
import com.example.klaf.pojo.Desk;
import com.example.klaf.screens.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class CheckerJobService extends JobService {
    private static boolean canceled = true;
    private KlafDatabase database;
    private List<Desk> desks = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        database = KlafDatabase.getInstance(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                desks.addAll(database.deskDao().getDeckList());
                // if() check desks and show notification if it's needed

                DateWorker dateWorker = new DateWorker();
                NotificationAssembler notification = new NotificationAssembler(getApplicationContext());

                notification.setNotification(dateWorker.getFormattedCurrentTime(), "קלף");
                notification.showNotification();


                if (!canceled) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), CheckerJobService.class);
                    JobInfo.Builder infoBuilder = new JobInfo.Builder(1234567, componentName);
                    infoBuilder
                            .setMinimumLatency(dateWorker.getCurrentDate() + 600_000)
                            .setOverrideDeadline(600_000)
                            .setPersisted(true);
                    JobInfo jobInfo = infoBuilder.build();
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    jobScheduler.schedule(jobInfo);
                }
                jobFinished(params, false);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


}
