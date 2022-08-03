package com.example.weather.todoapp.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.weather.todoapp.MainActivity;
import com.example.weather.todoapp.R;

public class NotificationBuilder {
    private static String channelId = "123";

    public static Notification getNotification(Context activityContext, String title, String date) {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(activityContext, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activityContext);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activityContext, channelId);
        builder.setContentTitle(title);
        builder.setContentText(date);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);
        builder.setSmallIcon(R.drawable.ic_add_24);
        return builder.build();
    }
}
