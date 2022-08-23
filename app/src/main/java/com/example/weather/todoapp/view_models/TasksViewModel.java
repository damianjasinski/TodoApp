package com.example.weather.todoapp.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.models.Task;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class TasksViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> filteringCategory = new MutableLiveData<>("");
    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
    RealmResults<Task> RealmTasks;
    private final Realm realm;

    {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .name("test4")
                .build();
        realm = Realm.getInstance(config);
        RealmTasks = realm.where(Task.class).sort("execDateTimeEpoch", Sort.DESCENDING).findAll();
        if (RealmTasks.size() > 0) {
            setTasks(RealmTasks);
        }
        RealmTasks.addChangeListener(realmTasks -> tasks.postValue(realmTasks));
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public RealmResults<Category> getCategories() {
        return realm.where(Category.class).findAll();
    }

    public LiveData<String> getFilteringCategory() {
        return filteringCategory;
    }

    public void setFilteringCategory(String filteringCategory) {
        if ("".equals(filteringCategory)) {
            setTasks(RealmTasks);
        } else {
            List<Task> tasks = RealmTasks.stream().filter(task -> task.getCategory().getName().equals(filteringCategory)).collect(Collectors.toList());
            setTasks(tasks);
        }
        this.filteringCategory.setValue(filteringCategory);
    }


//    public List<Task> getFilteredTasks() {
//        return RealmTasks.stream().filter(task -> task.getCategory().getName().equals(filteringCategory.getValue())).collect(Collectors.toList());
//    }

    public void setTasks(List<Task> tasks) {
        this.tasks.setValue(tasks);
    }

    public void hideOutdated() {
        setTasks(getTasks().getValue().stream().filter(task -> task.getExecDateTimeEpoch() > LocalDateTime.now().toEpochSecond(zoneOffset)).collect(Collectors.toList()));
    }

    public void showOutdated() {
        setFilteringCategory(getFilteringCategory().getValue());
    }

}
