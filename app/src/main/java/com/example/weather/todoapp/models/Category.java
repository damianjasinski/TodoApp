package com.example.weather.todoapp.models;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {

    @PrimaryKey
    private String name;
    @LinkingObjects("category")
    private final RealmResults<Task> tasks = null;


    public Category(String name) {
        this.name = name;
    }

    public Category () {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmResults<Task> getTasks() {
        return tasks;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
