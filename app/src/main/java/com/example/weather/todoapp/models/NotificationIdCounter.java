package com.example.weather.todoapp.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationIdCounter extends RealmObject {
    @PrimaryKey
    String id;
    int notificationCounter;

    public int getNotificationCounter() {
        return notificationCounter;
    }

    public void setNotificationCounter(int notificationCounter) {
        this.notificationCounter = notificationCounter;
    }

    public NotificationIdCounter() {
        notificationCounter = 0;
    }

}
