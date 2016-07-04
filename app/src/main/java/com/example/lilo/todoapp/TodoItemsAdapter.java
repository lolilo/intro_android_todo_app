package com.example.lilo.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lilo.todoapp.models.TodoItem;

import java.util.ArrayList;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {
    public TodoItemsAdapter(Context context, ArrayList<TodoItem> toDoItems) {
        super(context, 0, toDoItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem toDoItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
        // Populate the data into the template view using the data object
        tvName.setText(toDoItem.title);
        tvDueDate.setText(" " + toDoItem.dueDate);
        // Return the completed view to render on screen
        return convertView;
    }
}
