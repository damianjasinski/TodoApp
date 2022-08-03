package com.example.weather.todoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.todoapp.R;
import com.example.weather.todoapp.databinding.TaskItemBinding;
import com.example.weather.todoapp.models.Task;
import com.example.weather.todoapp.util.DateConverter;

import java.time.LocalDateTime;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    private List<Task> tasks;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskName;
        private final TextView taskDesc;
        private final TextView taskCategory;
        private final TextView taskDateTime;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            taskName = view.findViewById(R.id.task_name);
            taskDesc = view.findViewById(R.id.task_desc);
            taskCategory = view.findViewById(R.id.task_category);
            taskDateTime = view.findViewById(R.id.task_datetime);
        }

        public TextView getTaskName() {
            return taskName;
        }

        public TextView getTaskDesc() {
            return taskDesc;
        }

        public TextView getTaskCategory() {
            return taskCategory;
        }

        public TextView getTaskDateTime() {
            return taskDateTime;
        }
    }

    public RvAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTaskName().setText(tasks.get(position).getTitle());
        viewHolder.getTaskDesc().setText(tasks.get(position).getDesc());
        viewHolder.getTaskCategory().setText(tasks.get(position).getCategory().getName());
        viewHolder.getTaskDateTime().setText(DateConverter.getPrettyLocalDateTime(tasks.get(position).getExecDateTimeEpoch()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setNewTasks(List<Task> tasks) {
        this.tasks = tasks;
        this.notifyDataSetChanged();
    }
}
