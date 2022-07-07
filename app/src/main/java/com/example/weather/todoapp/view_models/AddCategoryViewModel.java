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

public class AddCategoryViewModel extends ViewModel {

    private final Realm realm;
    private MutableLiveData<String> categoryName = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .name("test4")
                .build();
        realm = Realm.getInstance(config);
        RealmResults<Category> RealmCategories = realm.where(Category.class).findAll();
        setCategories(RealmCategories);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.setValue(categoryName);
    }

    public LiveData<String> getCategoryName() {
        return this.categoryName;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }

    public void putCategory() {
        realm.executeTransaction(transactionRealm -> {
            Category category = realm.createObject(Category.class, categoryName.getValue());
        });
    }
}
