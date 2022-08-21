package com.example.weather.todoapp.view_models;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.models.NotificationIdCounter;
import com.example.weather.todoapp.models.Task;
import com.example.weather.todoapp.notification.NotificationBuilder;
import com.example.weather.todoapp.notification.NotificationScheduler;
import com.example.weather.todoapp.util.DateConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class EditTaskViewModel extends ViewModel {

    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
    private MutableLiveData<Task> taskToEdit = new MutableLiveData<>();
    private MutableLiveData<String> taskName = new MutableLiveData<>();
    private MutableLiveData<String> taskDesc = new MutableLiveData<>();
    private MutableLiveData<String> taskUri = new MutableLiveData<>();
    private MutableLiveData<Integer> chosenCategory = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<LocalDateTime> selectedDateTime = new MutableLiveData<>();
    public RealmResults<Category> RealmCategories;
    private long taskId;
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

    public void getAndSetTaskToEdit(long taskId) {
        Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
        this.taskId = taskId;
        setTaskToEdit(task);
    }

    public void setTaskToEdit(Task taskToEdit) {
        this.taskToEdit.postValue(taskToEdit);
        setTaskName(taskToEdit.getTitle());
        setTaskDesc(taskToEdit.getDesc());
        setTaskUri(taskToEdit.getUri());
        setChosenCategory(RealmCategories.indexOf(taskToEdit.getCategory()));
        setSelectedDateTime(DateConverter.epochToDateTime(taskToEdit.getExecDateTimeEpoch()));
    }

    public void setSelectedDateTime(LocalDateTime selectedDateTime) {
        this.selectedDateTime.postValue(selectedDateTime);
    }

    public void setTaskUri(String taskUri) {
        this.taskUri.setValue(taskUri);
    }

    public void setTaskName(String taskName) {
        this.taskName.setValue(taskName);
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc.setValue(taskDesc);
    }

    public void setChosenCategory(int categoryPosition) {
        this.chosenCategory.setValue(categoryPosition);
    }

    public LiveData<Integer> getChosenCategory() {
        return chosenCategory;
    }

    public LiveData<String> getTaskName() {
        return taskName;
    }

    public LiveData<String> getTaskDesc() {
        return taskDesc;
    }

    public LiveData<String> getTaskUri() {
        return taskUri;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }



    public LiveData<LocalDateTime> getSelectedDateTime() {
        return selectedDateTime;
    }

    public void clean() {
        taskDesc.setValue("");
        taskName.setValue("");
        chosenCategory.setValue(-1);
    }

    public void updateTask() {
        realm.executeTransaction(transactionRealm -> {
            Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
            task.setTitle(taskName.getValue());
            task.setDesc(taskDesc.getValue());

            task.setExecDateTimeEpoch(selectedDateTime.getValue().toEpochSecond(zoneOffset));
            Category category = realm.where(Category.class).equalTo("name", categories.getValue().get(chosenCategory.getValue()).toString()).findFirst();
            task.setCategory(category);
            System.out.println(category);
        });
    }

    public void setNotification(Context activity, String title) {
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
    }
}
