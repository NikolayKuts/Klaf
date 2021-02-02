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
    private Context context;
    private Notification notification;
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ID");
        builder.setSmallIcon(R.drawable.ic_notification_desk)
                .setContentTitle(title)
                .setContentText(massage)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notification = builder.build();
    }

    public void showNotification() {
        notificationManager.notify(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ID", "channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
