package com.example.weather.todoapp.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.models.Task;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AddTaskViewModel extends ViewModel {

    private MutableLiveData<String> taskName = new MutableLiveData<>();
    private MutableLiveData<String> taskDesc = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    RealmResults<Category> RealmCategories;
    private final Realm realm;


    {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .name("testRealm")
                .inMemory()
                .build();
        realm = Realm.getInstance(config);
        RealmCategories = realm.where(Category.class).findAll();
        if (RealmCategories.size() > 0) {
           setCategories(RealmCategories);
        }
    }

    public void setTaskName(String taskName) {
        this.taskName.setValue(taskName);
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc.setValue(taskDesc);
    }

    public LiveData<String> getTaskName() {
        return taskName;
    }

    public LiveData<String> getTaskDesc() {
        return taskDesc;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }

    public void addNewTask() {
        realm.executeTransaction(transactionRealm -> {
            Number maxId = realm.where(Task.class).max("id");
            Long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            Task task = realm.createObject(Task.class, nextId);
            task.setTitle(taskName.getValue());
            task.setDesc(taskDesc.getValue());
        });
    }

}
