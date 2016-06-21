package com.example.lilo.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.lilo.todoapp.database.TodoItemDatabase;
import com.example.lilo.todoapp.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> todoItems;
    ArrayAdapter<String> aToDoAdapter;
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
                todoItemDeleted.title = todoItems.get(position);
                databaseHelper.deleteTodoItem(todoItemDeleted);
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

    public void populateArrayItems() {
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    private void readItems() {
        List<TodoItem> todoItemsFromDb = databaseHelper.getAllTodoItems();
        todoItems = new ArrayList<String>();
        for (TodoItem todoItem : todoItemsFromDb) {
            todoItems.add(todoItem.title);
        }
    }

    private void writeItems() {
        for (String todoItemTitle : todoItems) {
            TodoItem todoItem = new TodoItem();
            todoItem.title = todoItemTitle;
            databaseHelper.updateTodoItem(todoItem);
        }
    }

    public void onAddItem(View view) {
        aToDoAdapter.add(etEditText.getText().toString());
        TodoItem todoItem = new TodoItem();
        todoItem.title = etEditText.getText().toString();
        etEditText.setText("");
        databaseHelper.addTodoItem(todoItem);
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
            todoItems.set(editedItemPosition, editedItem);
            aToDoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }
}
