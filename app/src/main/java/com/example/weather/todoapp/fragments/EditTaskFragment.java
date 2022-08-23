package com.example.weather.todoapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.todoapp.R;
import com.example.weather.todoapp.databinding.FragmentAddTaskBinding;
import com.example.weather.todoapp.databinding.FragmentEditTaskBinding;
import com.example.weather.todoapp.util.DateConverter;
import com.example.weather.todoapp.view_models.EditTaskViewModel;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class EditTaskFragment extends Fragment {

    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
    private FragmentEditTaskBinding binding;
    private EditTaskViewModel editTaskViewModel;
    int chosenCategoryPos = -1;

    public EditTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save current view model state
        editTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
        editTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
        if (binding.categorySelectView.getListSelection() != ListView.INVALID_POSITION) {
            editTaskViewModel.setChosenCategory(binding.categorySelectView.getListSelection());
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
        binding = FragmentEditTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        editTaskViewModel = new ViewModelProvider(requireActivity()).get(EditTaskViewModel.class);
        long taskId = EditTaskFragmentArgs.fromBundle(getArguments()).getTaskIdToEdit();
        editTaskViewModel.getAndSetTaskToEdit(taskId);
        binding.categorySelectView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCategoryPos = i;
            }
        });

        binding.link.setOnClickListener(x -> {
            try {
                openImage(Uri.parse(editTaskViewModel.getTaskUri().getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        binding.addNotifyCheckbox.setOnClickListener(x -> {
            editTaskViewModel.isNotificationCbChanged = true;
            editTaskViewModel.setNotificationChecked(binding.addNotifyCheckbox.isChecked());
        });


        observeEditTaskVM();
        addCategoryBtnInit();
        dateTimePickInit();
        updateTaskBtnInit();
        return root;
    }

    private void addCategoryBtnInit() {
        binding.createCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chosenCategoryPos == -1) {
                    editTaskViewModel.setChosenCategory(binding.categorySelectView.getListSelection());
                }
                Navigation.findNavController(view).navigate(R.id.action_editTaskFragment_to_addCategoryFragment);
            }
        });
    }


    private void observeEditTaskVM() {
        editTaskViewModel.getTaskName().observe(getViewLifecycleOwner(), taskName -> {
            binding.taskNameView.setText(taskName);
        });

        editTaskViewModel.getTaskDesc().observe(getViewLifecycleOwner(), taskDesc -> {
            binding.taskDescView.setText(taskDesc);
        });

        editTaskViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.category_dropdown_list_item, categories);
            binding.categorySelectView.setAdapter(adapter);
            binding.categorySelectView.setText(editTaskViewModel.getCategories().getValue().get(editTaskViewModel.getChosenCategory().getValue()).toString(), false);
            chosenCategoryPos = editTaskViewModel.getChosenCategory().getValue();
        });

        editTaskViewModel.getChosenCategory().observe(getViewLifecycleOwner(), chosenCategoryPosition -> {
            binding.categorySelectView.setListSelection(chosenCategoryPosition);
        });

        editTaskViewModel.getSelectedDateTime().observe(getViewLifecycleOwner(), dateTime -> {
            binding.datePickerView.setText(DateConverter.getPrettyLocalDateTime(dateTime.toEpochSecond(zoneOffset)));
        });

        editTaskViewModel.getCreationDateTime().observe(getViewLifecycleOwner(), dateTime -> {
            binding.createdAt.setText(binding.createdAt.getText() + " " + DateConverter.getPrettyLocalDateTime(dateTime.toEpochSecond(zoneOffset)));
        });

        editTaskViewModel.isNotificationChecked().observe(getViewLifecycleOwner(), checked -> {
            binding.addNotifyCheckbox.setChecked(checked);
        });

        editTaskViewModel.getTaskUri().observe(getViewLifecycleOwner(), uri -> {
            binding.link.setText(uri);
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
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(editTaskViewModel.getSelectedDateTime().getValue().getHour())
                                .setMinute(editTaskViewModel.getSelectedDateTime().getValue().getMinute())
                                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                                .build();
                timePicker.show(requireActivity().getSupportFragmentManager(), "tag");
                picker.addOnPositiveButtonClickListener(x -> {
                    LocalDate selectedDate = Instant.ofEpochMilli(Long.parseLong(picker.getSelection().toString())).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDateTime combinedDateAndTime = LocalDateTime.of(selectedDate, LocalTime.of(timePicker.getHour(), timePicker.getMinute()));
                    Log.i("time", combinedDateAndTime.toString());
                    editTaskViewModel.setSelectedDateTime(combinedDateAndTime);
                });
            }
        });
    }

    private void updateTaskBtnInit() {
        binding.createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    editTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
                    editTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
                    if (chosenCategoryPos != -1) {
                        editTaskViewModel.setChosenCategory(chosenCategoryPos);
                    }
                    editTaskViewModel.updateTask(requireActivity());
                    editTaskViewModel.clean();
                    Navigation.findNavController(view).navigate(R.id.action_editTaskFragment_to_tasksFragment);
                }
            }
        });
    }


    private void openImage(Uri uri) throws IOException {
        Intent openImage = new Intent(Intent.ACTION_VIEW, uri);
        openImage.setDataAndType(uri, "image/*");
        startActivity(openImage);
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
        if (editTaskViewModel.getSelectedDateTime() == null) {
            isOk = false;
            binding.datePickerView.setError("Select date and time!");
        }
        return isOk;
    }

}
