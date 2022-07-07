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

import com.example.weather.todoapp.models.Category;
import com.example.weather.todoapp.view_models.AddCategoryViewModel;
import com.example.weather.todoapp.view_models.AddTaskViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddCategoryFragment extends Fragment {

    private TextInputEditText newCategoryName;
    private AutoCompleteTextView categoriesDropdown;
    private Button createCategoryButton;
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_category, container, false);

        newCategoryName = root.findViewById(R.id.category_name_view);
        createCategoryButton = root.findViewById(R.id.create_category_button);
        addCategoryViewModel = new ViewModelProvider(requireActivity()).get(AddCategoryViewModel.class);
        addCategoryButtonInit();
        observeVM();
        categoriesDropdown = root.findViewById(R.id.category_select_view);
        return root;
    }

    public void addCategoryButtonInit() {
        createCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    addCategoryViewModel.setCategoryName(newCategoryName.getText().toString());
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
            categoriesDropdown.setAdapter(adapter);
        });
    }

    private boolean validateFields() {
        boolean isOk = true;
        if (newCategoryName.getText().length() < 2) {
            isOk = false;
            newCategoryName.setError("Atleast 2 signs please!");
        }
        return isOk;
    }
}