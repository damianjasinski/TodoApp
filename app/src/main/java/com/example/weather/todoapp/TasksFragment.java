package com.example.weather.todoapp;

import android.os.Bundle;

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
import com.example.weather.todoapp.models.Task;
import com.example.weather.todoapp.view_models.TasksViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private ExtendedFloatingActionButton addTaskBtn;
    private TasksViewModel viewModel;
    private RvAdapter adapter;

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
    }
}