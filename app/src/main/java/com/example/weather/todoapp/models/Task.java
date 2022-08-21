package com.example.weather.todoapp.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Task extends RealmObject {

    @PrimaryKey
    private Long id;
    String title;
    String desc;
    String uri;
    Long creationDateTimeEpoch;
    Long execDateTimeEpoch;
    Category category;
    boolean isDone = false;
    boolean isNotificationOn = true;

    public Task(String title, String desc) {
    }

    public Task() {

    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getCreationDateTimeEpoch() {
        return creationDateTimeEpoch;
    }

    public void setCreationDateTimeEpoch(Long creationDateTimeEpoch) {
        this.creationDateTimeEpoch = creationDateTimeEpoch;
    }

    public Long getExecDateTimeEpoch() {
        return execDateTimeEpoch;
    }

    public void setExecDateTimeEpoch(Long execDateTimeEpoch) {
        this.execDateTimeEpoch = execDateTimeEpoch;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isNotificationOn() {
        return isNotificationOn;
    }

    public void setNotificationOn(boolean notificationOn) {
        isNotificationOn = notificationOn;
    }
}
