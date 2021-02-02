package com.example.klaf.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;

import com.example.klaf.DateWorker;
import com.example.klaf.notifications.NotificationAssembler;

public class CheckerJobService extends JobService {
    private static boolean canceled = true;

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // if() check desks and show notification if it's needed
                DateWorker dateWorker = new DateWorker();
                NotificationAssembler notification = new NotificationAssembler(getApplicationContext());
                notification.setNotification(Long.toString(dateWorker.getCurrentDate()), "title");
                notification.showNotification();

                if (!canceled) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), CheckerJobService.class);
                    JobInfo.Builder infoBuilder = new JobInfo.Builder(1234567, componentName);
                    infoBuilder.setMinimumLatency(10000)
                            .setOverrideDeadline(10000);
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
