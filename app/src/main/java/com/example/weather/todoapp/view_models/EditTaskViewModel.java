package com.example.weather.todoapp.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.todoapp.models.Task;

public class EditTaskViewModel extends ViewModel {

    private MutableLiveData<Task> taskToEdit;

    public void setTaskToEdit(Task taskToEdit) {
        this.taskToEdit.postValue(taskToEdit);
    }

}
