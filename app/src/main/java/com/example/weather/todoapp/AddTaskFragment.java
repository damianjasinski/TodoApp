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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class AddTaskFragment extends Fragment {

    private TasksViewModel tasksViewModel;
    private AddTaskViewModel addTaskViewModel;
    private TextInputEditText taskName;
    private TextInputEditText taskDesc;
    private TextInputEditText datePicker;
    private AutoCompleteTextView categoriesDropdown;
    private Button createTaskButton;
    private Button addCategoryButton;
    int chosenCategoryPos = -1;

    public AddTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save current view model state
        addTaskViewModel.setTaskName(taskName.getText().toString());
        addTaskViewModel.setTaskDesc(taskDesc.getText().toString());
        if (categoriesDropdown.getListSelection() != ListView.INVALID_POSITION) {
            addTaskViewModel.setChosenCategory(categoriesDropdown.getListSelection());
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
        addTaskViewModel = new ViewModelProvider(requireActivity()).get(AddTaskViewModel.class);
        createTaskButton = root.findViewById(R.id.create_task_view);
        addCategoryButton = root.findViewById(R.id.create_category_button);
        taskName = root.findViewById(R.id.task_name_view);
        taskDesc = root.findViewById(R.id.task_desc_view);
        categoriesDropdown = root.findViewById(R.id.category_select_view);
        categoriesDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCategoryPos = i;
            }
        });
        createTaskBtnInit();
        addCategoryBtnInit();
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

        addTaskViewModel.getChosenCategory().observe(getViewLifecycleOwner(), chosenCategoryPosition -> {
            this.categoriesDropdown.setListSelection(chosenCategoryPosition);
        });

        addTaskViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.category_dropdown_list_item, categories);
            categoriesDropdown.setAdapter(adapter);
        });
    }

    private void createTaskBtnInit() {
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    addTaskViewModel.setTaskName(taskName.getText().toString());
                    addTaskViewModel.setTaskDesc(taskDesc.getText().toString());
                    if (chosenCategoryPos != -1) {
                        addTaskViewModel.setChosenCategory(chosenCategoryPos);
                    }
                    addTaskViewModel.addNewTask();
                    addTaskViewModel.clean();
                    Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_tasksFragment);
                }
            }
        });
    }

    private void addCategoryBtnInit() {
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskViewModel.setTaskName(taskName.getText().toString());
                addTaskViewModel.setTaskDesc(taskDesc.getText().toString());
                if (chosenCategoryPos == -1) {
                    addTaskViewModel.setChosenCategory(categoriesDropdown.getListSelection());
                }
                Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_addCategoryFragment);
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
        if (chosenCategoryPos == -1 ) {
            Toast.makeText(requireActivity(), "Select category or add new if it's your first!", Toast.LENGTH_LONG).show();
            isOk = false;
            categoriesDropdown.setError("Select one!");
        }
        return isOk;
    }

}