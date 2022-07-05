package com.example.weather.todoapp.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Task extends RealmObject {

    @PrimaryKey
    private Long id;
    String title;
    String desc;
    Long creationDateTimeEpoch;
    Long execDateTimeEpoch;
    Category category;
    boolean isDone = false;
    boolean isNotificationOn = true;

    public Task(String title, String desc) {
        this.title = title;
        this.desc = desc;
//        this.creationDateTimeEpoch = creationDateTimeEpoch;
//        this.execDateTimeEpoch = execDateTimeEpoch;
//        this.isDone = isDone;
//        this.isNotificationOn = isNotificationOn;
    }

    public Task() {

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
