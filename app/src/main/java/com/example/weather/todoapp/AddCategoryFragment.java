package com.example.weather.todoapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.weather.todoapp.databinding.FragmentAddCategoryBinding;
import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.view_models.AddCategoryViewModel;
import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddCategoryFragment extends Fragment {

    private FragmentAddCategoryBinding binding;
    private AddCategoryViewModel addCategoryViewModel;

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        View root = binding.getRoot();
        addCategoryViewModel = new ViewModelProvider(requireActivity()).get(AddCategoryViewModel.class);
        addCategoryButtonInit();
        observeVM();
        return root;
    }

    public void addCategoryButtonInit() {
        binding.createCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    addCategoryViewModel.setCategoryName(binding.categoryNameView.getText().toString());
                    try {
                        addCategoryViewModel.putCategory();
                        Navigation.findNavController(view).navigate(R.id.action_addCategoryFragment_to_addTaskFragment2);
                    } catch (RealmPrimaryKeyConstraintException pkViolation) {
                        Toast.makeText(requireActivity(), "You can't add duplicates!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void observeVM() {
        addCategoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter<String> adapter = new ArrayAdapter(requireContext(), R.layout.category_dropdown_list_item, categories);
            binding.categorySelectView.setAdapter(adapter);
        });
    }

    private boolean validateFields() {
        boolean isOk = true;
        if (binding.categoryNameView.getText().length() < 2) {
            isOk = false;
            binding.categoryNameView.setError("Atleast 2 signs please!");
        }
        return isOk;
    }
}