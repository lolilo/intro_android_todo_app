package com.example.lilo.todoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lilo.todoapp.database.TodoItemDatabase;
import com.example.lilo.todoapp.models.TodoItem;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MainActivity extends AppCompatActivity {
    ArrayList<TodoItem> todoItems;
    TodoItemsAdapter aToDoAdapter;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 20;
    TodoItemDatabase databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = TodoItemDatabase.getInstance(this);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem todoItemDeleted = new TodoItem();
                todoItems.get(position);
                todoItemDeleted.title = todoItems.get(position).title;
                databaseHelper.deleteTodoItem(todoItemDeleted); //TODO: Delete by id.
                todoItems.remove(position);
                aToDoAdapter.notifyDataSetChanged();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItemView(todoItems.get(position).title, position);
            }
        });
    }

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
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            tvName.setText(toDoItem.title);
//            tvHome.setText(user.hometown);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void onAddItem(View view) {
        TodoItem todoItem = new TodoItem();
        todoItem.title = etEditText.getText().toString();
        etEditText.setText("");
        todoItem.id = (int) (databaseHelper.addTodoItem(todoItem));
        todoItems.add(todoItem);
    }

    public void onDeleteAll(View view) {
        databaseHelper.deleteAllTodoItems();
        todoItems.clear();
        aToDoAdapter.clear();
    }

    private void launchEditItemView(String todoItem, int itemPosition) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("todoItem", todoItem);
        i.putExtra("todoItemPosition", itemPosition);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedItem = data.getExtras().getString("editedItem");
            int editedItemPosition = data.getExtras().getInt("itemPosition");
            TodoItem todoItem = new TodoItem();
            todoItem.title = editedItem;
            todoItems.set(editedItemPosition, todoItem);
            aToDoAdapter.notifyDataSetChanged();
            updateDb(data, editedItem);
        }
    }


    // TODO: create MainActivityHelper with Dagger
    private void populateArrayItems() {
        readItems();
        aToDoAdapter = new TodoItemsAdapter(this, todoItems);
    }

    private void readItems() {
        todoItems = databaseHelper.getAllTodoItems();
    }

    private TodoItem getDbTodoItemByTitle(String title) {
        for (TodoItem todoItem : todoItems) {
            if (todoItem.title.equals(title)) {
                return todoItem;
            }
        }
        throw new NoSuchElementException("Error while looking for existing todo item.");
    }

    private void updateDb(Intent data, String editedItem) {
        String originalItem = data.getExtras().getString("originalItem");
        TodoItem editedTodoItemFromDb = getDbTodoItemByTitle(originalItem);
        editedTodoItemFromDb.title = editedItem;
        databaseHelper.updateTodoItem(editedTodoItemFromDb);
    }
}
