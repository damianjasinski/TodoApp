package com.example.weather.todoapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.ParcelFileDescriptor;
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
import com.example.weather.todoapp.util.DateConverter;
import com.example.weather.todoapp.util.FileCopyClass;
import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.FileDescriptor;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AddTaskFragment extends Fragment {

    private Uri imageUri;
    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
    private FragmentAddTaskBinding binding;
    private TasksViewModel tasksViewModel;
    private AddTaskViewModel addTaskViewModel;
    private Bitmap loadedImage;
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

        //todo save uri in task model
        getAttachmentURI();
        createTaskBtnInit();
        dateTimePickInit();
        addCategoryBtnInit();
        observeAddTaskVM();
        return root;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
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

        addTaskViewModel.getSelectedDateTime().observe(getViewLifecycleOwner(), date -> {
            binding.datePickerView.setText(DateConverter.getPrettyLocalDateTime(date.toEpochSecond(zoneOffset)));
        });

        addTaskViewModel.getAttachmentUri().observe(getViewLifecycleOwner(), uri -> {
            binding.link.setText(uri.getPath());
            imageUri = uri;
        });
    }

    private void createTaskBtnInit() {
        binding.createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    if (imageUri != null) {
                        FileCopyClass fileCopyClass = new FileCopyClass(getContext());
                        addTaskViewModel.setAttachmentUri(fileCopyClass.copyAttachmenToExternal(imageUri));
                    }
                    addTaskViewModel.setTaskName(binding.taskNameView.getText().toString());
                    addTaskViewModel.setTaskDesc(binding.taskDescView.getText().toString());
                    if (chosenCategoryPos != -1) {
                        addTaskViewModel.setChosenCategory(chosenCategoryPos);
                    }
                    addTaskViewModel.addNewTask();
                    if (binding.addNotifyCheckbox.isChecked()) {
                        addTaskViewModel.setNotification(requireActivity(), addTaskViewModel.getTaskName().getValue());
                    }
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
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(12)
                                .setMinute(00)
                                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                                .build();
                timePicker.show(requireActivity().getSupportFragmentManager(), "tag");
                picker.addOnPositiveButtonClickListener(x -> {
                    LocalDate selectedDate = Instant.ofEpochMilli(Long.parseLong(picker.getSelection().toString())).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDateTime combinedDateAndTime = LocalDateTime.of(selectedDate, LocalTime.of(timePicker.getHour(), timePicker.getMinute()));
                    Log.i("time", combinedDateAndTime.toString());
                    addTaskViewModel.setSelectedDateTime(combinedDateAndTime);
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

    private void getAttachmentURI() {
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Log.i("open", uri.getPath());
                        try {
                            loadedImage = getBitmapFromUri(uri);
                            imageUri = uri;
                            binding.link.setText(uri.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        binding.link.setOnClickListener(x -> {
            try {
                openImage(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        binding.attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass in the mime type you'd like to allow the user to select
                // as the input
                mGetContent.launch("image/*");
                binding.imageView.setImageBitmap(loadedImage);
            }
        });
    }

    private void openImage(Uri uri) throws IOException {
        Intent openImage = new Intent(Intent.ACTION_VIEW, uri);
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
        if (addTaskViewModel.getSelectedDateTime() == null) {
            isOk = false;
            binding.datePickerView.setError("Select date and time!");
        }
        return isOk;
    }

}