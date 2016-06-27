package com.example.lilo.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.lilo.todoapp.database.TodoItemDatabase;
import com.example.lilo.todoapp.models.TodoItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<TodoItem> todoItems;
    com.example.lilo.todoapp.TodoItemsAdapter aToDoAdapter;
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
                launchEditItemView(todoItems.get(position), position);
            }
        });
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

    private void launchEditItemView(TodoItem todoItem, int itemPosition) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("todoItemTitle", todoItem.title);
        i.putExtra("todoItemPosition", itemPosition);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedItem = data.getExtras().getString("editedItem");
            int editedItemPosition = data.getExtras().getInt("itemPosition");
            TodoItem editedTodoItem = todoItems.get(editedItemPosition);
            editedTodoItem.title = editedItem;
            aToDoAdapter.notifyDataSetChanged();
            databaseHelper.updateTodoItem(editedTodoItem);
        }
    }


    // TODO: create MainActivityHelper with Dagger
    private void populateArrayItems() {
        todoItems = databaseHelper.getAllTodoItems();
        aToDoAdapter = new TodoItemsAdapter(this, todoItems);
    }
}
