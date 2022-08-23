package com.example.weather.todoapp.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class NotificationScheduler {

    private static ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

    public static void scheduleNotification(Context activity, Notification notification, Integer notificationId, Long epochTime) {
        if (epochTime < LocalDateTime.now().toEpochSecond(zoneOffset)) {
            return;
        }
        Intent notificationIntent = new Intent(activity, MyNotificationPublisher.class);
        notificationIntent.putExtra("notificationId", notificationId);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        //trigger 1h before
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (epochTime - 3600) * 1000, pendingIntent);
        Log.i("Notification", "scheduled with id: " + notificationId + "epoch time is: " + epochTime );
    }
}
