package com.example.klaf.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.klaf.R;
import com.example.klaf.screens.MainActivity;

public class NotificationAssembler {
    private static final String CHANEL_NAME = "chanel_1";
    private static final String CHANEL_ID = "ID";
    public static final int NOTIFICATION_ID_DEFAULT = 1;

    private Context context;
    private Notification notification;
    private int notificationId;
    private NotificationManager notificationManager;

    public NotificationAssembler(Context context) {
        this.context = context;
    }

    public void setNotification(String massage, String title) {
        Intent intentForCalling = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentForCalling,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification_desk)
                .setContentTitle(title)
                .setContentText(massage)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notification = builder.build();
    }

    public void showNotification(int notificationId) {
        notificationManager.notify(notificationId, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
