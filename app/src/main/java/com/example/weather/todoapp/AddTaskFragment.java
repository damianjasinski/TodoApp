package com.example.weather.todoapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.todoapp.databinding.FragmentAddTaskBinding;
import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;
    private TasksViewModel tasksViewModel;
    private AddTaskViewModel addTaskViewModel;
    int chosenCategoryPos = -1;

    public AddTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save current view model state
        addTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
        addTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
        if (binding.categorySelectView.getListSelection() != ListView.INVALID_POSITION) {
            addTaskViewModel.setChosenCategory(binding.categorySelectView.getListSelection());
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
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        addTaskViewModel = new ViewModelProvider(requireActivity()).get(AddTaskViewModel.class);
        binding.categorySelectView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCategoryPos = i;
            }
        });
        createTaskBtnInit();
        dateTimePickInit();
        addCategoryBtnInit();
        observeAddTaskVM();
        return root;
    }

    private void observeAddTaskVM() {
        addTaskViewModel.getTaskName().observe(getViewLifecycleOwner(), taskName -> {
            binding.taskNameView.setText(taskName);
        });

        addTaskViewModel.getTaskDesc().observe(getViewLifecycleOwner(), taskDesc -> {
            binding.taskDescView.setText(taskDesc);
        });

        addTaskViewModel.getChosenCategory().observe(getViewLifecycleOwner(), chosenCategoryPosition -> {
            binding.categorySelectView.setListSelection(chosenCategoryPosition);
        });

        addTaskViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.category_dropdown_list_item, categories);
            binding.categorySelectView.setAdapter(adapter);
        });

        addTaskViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            LocalDate selectedDate = Instant.ofEpochMilli(Long.parseLong(date)).atZone(ZoneId.systemDefault()).toLocalDate();
            String pickedTime = addTaskViewModel.getSelectedTime().getValue();
            binding.datePickerView.setText(selectedDate.getDayOfMonth() + "/" + selectedDate.getMonth().toString().toLowerCase() + "/" + selectedDate.getYear() + " " + pickedTime);
        });
    }

    private void createTaskBtnInit() {
        binding.createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    addTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
                    addTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
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

    private void dateTimePickInit() {
        binding.datePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                picker.show(requireActivity().getSupportFragmentManager(), "date_pick");

                //select time
                MaterialTimePicker timePicker =
                        new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_12H)
                                .setHour(12)
                                .setMinute(10)
                                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                                .build();
                timePicker.show(requireActivity().getSupportFragmentManager(), "tag");
                timePicker.addOnPositiveButtonClickListener(view1 -> addTaskViewModel.setSelectedTime(timePicker.getHour() + ":" + timePicker.getMinute()));
                picker.addOnPositiveButtonClickListener(x -> {
                    addTaskViewModel.setSelectedDate(picker.getSelection().toString());
                });
            }
        });
    }

    private void addCategoryBtnInit() {
        binding.createCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
                addTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
                if (chosenCategoryPos == -1) {
                    addTaskViewModel.setChosenCategory(binding.categorySelectView.getListSelection());
                }
                Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_addCategoryFragment);
            }
        });
    }

    private boolean validateFields() {
        boolean isOk = true;
        if (binding.taskNameView.getText().length() == 0) {
            isOk = false;
            binding.taskNameView.setError("Can't be empty!");
        }
        if (binding.taskDescView.getText().length() == 0) {
            isOk = false;
            binding.taskDescView.setError("Can't be empty!");
        }
        if (chosenCategoryPos == -1) {
            Toast.makeText(requireActivity(), "Select category or add new if it's your first!", Toast.LENGTH_LONG).show();
            isOk = false;
            binding.categorySelectView.setError("Select one!");
        }
        return isOk;
    }

}