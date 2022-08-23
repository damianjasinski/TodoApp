package com.example.weather.todoapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationCancel {

    public static void cancelNotification(Context activity, Notification notification, Long notificationId, Long epochTime) {
        Intent notificationIntent = new Intent(activity, MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationId", notificationId);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        //trigger 1h before
        alarmManager.cancel(pendingIntent);
    }
}
