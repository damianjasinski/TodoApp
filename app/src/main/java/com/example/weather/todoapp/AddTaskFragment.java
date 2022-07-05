package com.example.weather.todoapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class AddTaskFragment extends Fragment {

    private TasksViewModel tasksViewModel;
    private AddTaskViewModel addTaskViewModel;
    private TextInputEditText taskName;
    private TextInputEditText taskDesc;
    private AutoCompleteTextView categoriesDropdown;
    private Button createTaskButton;

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save current view model state
        addTaskViewModel.setTaskName(taskName.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public AddTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_task, container, false);

        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        addTaskViewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);
        createTaskButton = root.findViewById(R.id.create_task_view);
        taskName = root.findViewById(R.id.task_name_view);
        taskDesc = root.findViewById(R.id.task_desc_view);
        categoriesDropdown = root.findViewById(R.id.category_select_view);
        createTaskBtnInit();
        observeAddTaskVM();

        return root;
    }

    private void observeAddTaskVM() {
        addTaskViewModel.getTaskName().observe(getViewLifecycleOwner(), taskName -> {
            this.taskName.setText(taskName);
        });

        addTaskViewModel.getTaskDesc().observe(getViewLifecycleOwner(), taskDesc -> {
            this.taskDesc.setText(taskDesc);
        });

    }

    private void createTaskBtnInit() {
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    addTaskViewModel.setTaskName(taskName.getText().toString());
                    addTaskViewModel.setTaskDesc(taskDesc.getText().toString());
                    addTaskViewModel.addNewTask();
                    Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_tasksFragment);
                }
            }
        });
    }

    private boolean validateFields() {
        boolean isOk = true;
        if (taskName.getText().length() == 0) {
            isOk = false;
            taskName.setError("Can't be empty!");
        }
        if (taskDesc.getText().length() == 0) {
            isOk = false;
            taskDesc.setError("Can't be empty!");
        }
        if (categoriesDropdown.getText().length() == 0) {
            isOk = false;
            categoriesDropdown.setError("Select one!");
        }
        return isOk;
    }

}