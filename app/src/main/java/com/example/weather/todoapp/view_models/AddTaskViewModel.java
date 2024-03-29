package com.example.weather.todoapp.view_models;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.models.NotificationIdCounter;
import com.example.weather.todoapp.models.Task;
import com.example.weather.todoapp.notification.NotificationBuilder;
import com.example.weather.todoapp.notification.NotificationScheduler;
import com.example.weather.todoapp.util.DateConverter;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AddTaskViewModel extends ViewModel {

    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
    private MutableLiveData<String> taskName = new MutableLiveData<>();
    private MutableLiveData<String> taskDesc = new MutableLiveData<>();
    private MutableLiveData<Integer> chosenCategory = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<LocalDateTime> selectedDateTime = new MutableLiveData<>();
    private MutableLiveData<Uri> attachmentUri = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNotificationChecked = new MutableLiveData<>(true);
    RealmResults<Category> RealmCategories;
    private final Realm realm;

    {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .name("test4")
                .build();
        realm = Realm.getInstance(config);
        RealmCategories = realm.where(Category.class).findAll();
        if (RealmCategories.size() > 0) {
            setCategories(RealmCategories);
        }
        RealmCategories.addChangeListener(realmCategories -> categories.postValue(realmCategories));
    }

    public void setTaskName(String taskName) {
        this.taskName.setValue(taskName);
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc.setValue(taskDesc);
    }

    public void setAttachmentUri(Uri uri) {
        this.attachmentUri.setValue(uri);
    }

    public void setChosenCategory(int categoryPosition) {
        this.chosenCategory.setValue(categoryPosition);
    }

    public LiveData<Boolean> isNotificationChecked() {
        return isNotificationChecked;
    }

    public void setNotificationChecked(Boolean checked) {
        isNotificationChecked.postValue(checked);
    }

    public LiveData<String> getTaskName() {
        return taskName;
    }

    public LiveData<String> getTaskDesc() {
        return taskDesc;
    }

    public LiveData<Uri> getAttachmentUri() {
        return attachmentUri;
    }


    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Integer> getChosenCategory() {
        return chosenCategory;
    }


    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }


    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime.postValue(selectedDateTime);
    }

    public LiveData<LocalDateTime> getSelectedDateTime() {
        return selectedDateTime;
    }

    public void clean() {
        taskDesc.setValue("");
        taskName.setValue("");
        chosenCategory.setValue(-1);
    }

    public void addNewTask(Context context) {
        Long notificationId = 0L;
        if (isNotificationChecked != null && isNotificationChecked.getValue()) {
            notificationId = setNotification(context, taskName.getValue());
        }
        Long finalNotificationId = notificationId;
        realm.executeTransaction(transactionRealm -> {
            Number maxId = realm.where(Task.class).max("id");
            Long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            Task task = realm.createObject(Task.class, nextId);
            if (getAttachmentUri().getValue() != null) {
                task.setUri(getAttachmentUri().getValue().toString());
            }
            task.setNotificationId(finalNotificationId);
            task.setNotificationOn(isNotificationChecked.getValue());
            task.setTitle(taskName.getValue());
            task.setDesc(taskDesc.getValue());
            task.setExecDateTimeEpoch(selectedDateTime.getValue().toEpochSecond(zoneOffset));
            task.setCreationDateTimeEpoch(LocalDateTime.now().toEpochSecond(zoneOffset));
            Category category = realm.where(Category.class).equalTo("name", categories.getValue().get(chosenCategory.getValue()).toString()).findFirst();
            task.setCategory(category);
            System.out.println(category);
        });
    }

    private long setNotification(Context activity, String title) {
        Notification notification = NotificationBuilder.getNotification(activity, title, DateConverter.getPrettyLocalDateTime(selectedDateTime.getValue().toEpochSecond(zoneOffset)));
        if (realm.where(NotificationIdCounter.class).findFirst() == null) {
            realm.executeTransaction(r -> {
                realm.createObject(NotificationIdCounter.class, "notifCounter");
            });
        }
        NotificationIdCounter finalIdCounter = realm.where(NotificationIdCounter.class).findFirst();
        NotificationScheduler.scheduleNotification(activity, notification, finalIdCounter.getNotificationCounter(), selectedDateTime.getValue().toEpochSecond(zoneOffset));
        realm.executeTransaction(r -> {
            finalIdCounter.setNotificationCounter(finalIdCounter.getNotificationCounter() + 1);
        });
        return finalIdCounter.getNotificationCounter();
    }


}
