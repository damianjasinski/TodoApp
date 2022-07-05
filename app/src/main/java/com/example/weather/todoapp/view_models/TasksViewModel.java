package com.example.weather.todoapp.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Task;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class TasksViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks = new MutableLiveData<>(new ArrayList<>());
    RealmResults<Task> RealmTasks;
    private final Realm realm;

    {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .inMemory()
                .build();
        realm = Realm.getInstance(config);
        RealmTasks = realm.where(Task.class).findAll();
        if (RealmTasks.size() > 0) {
            tasks.postValue(RealmTasks);
        }
        RealmTasks.addChangeListener(realmTasks -> tasks.postValue(realmTasks));
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

//    public void setTasks(List<Task> tasks) {
//        this.tasks.setValue(tasks);
//    }
//
//    public void putTask(Task task) {
//        List<Task> newTasks = tasks.getValue();
//        newTasks.add(task);
//        this.tasks.postValue(newTasks);
//    }

}
