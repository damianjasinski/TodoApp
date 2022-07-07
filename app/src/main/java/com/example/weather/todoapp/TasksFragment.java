package com.example.weather.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weather.todoapp.adapters.RvAdapter;
import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.models.Task;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TasksFragment extends Fragment {

    private ExtendedFloatingActionButton addTaskBtn;
    private TasksViewModel viewModel;
    String filteringCategory;
    private RvAdapter adapter;
    int checkedDialogItem = -1;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.filter_action:
                    setupFilterDialog();
                    return true;
                default:
                    return false;
            }
        });
        //init view model
        viewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        List<Task> tasks = new ArrayList<>();
        observeDataChanges();

        //inflate layout and other lifecycle stuff
        RecyclerView recyclerView = root.findViewById(R.id.tasks_rv);
        //insert adapter into recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RvAdapter(tasks);
        recyclerView.setAdapter(adapter);

        addTaskBtn = root.findViewById(R.id.add_task_view);
        AddTaskBtnInit(root);

        return root;
    }

    private void AddTaskBtnInit(View root) {
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_tasksFragment_to_addTaskFragment);
            }
        });
    }

    private void observeDataChanges() {
        Log.i("VM", "data changed");
        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.setNewTasks(tasks);
        });
//        viewModel.getFilteringCategory().observe(getViewLifecycleOwner(), filteredTasks -> {
//            adapter.setNewTasks(viewModel.getFilteredTasks());
//        });
    }

    private void setupFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Choose filters");
        List<Category> categories = viewModel.getCategories();
        String[] items = categories.stream().map(Category::toString).collect(Collectors.toList()).toArray(new String[0]);
        builder.setSingleChoiceItems(items, checkedDialogItem, (dialog, which) -> {
            checkedDialogItem = which;
            filteringCategory = items[which];
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            viewModel.setFilteringCategory(filteringCategory);
        });
        builder.setNegativeButton("Clear",  (dialog, which) -> {
            checkedDialogItem = -1;
            viewModel.setFilteringCategory("");
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}