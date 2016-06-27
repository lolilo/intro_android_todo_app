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
import java.util.NoSuchElementException;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> todoItems;
    List<TodoItem> DbTodoItems;
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
        DbTodoItems = databaseHelper.getAllTodoItems();
        todoItems = new ArrayList<String>();
        for (TodoItem todoItem : DbTodoItems) {
            todoItems.add(todoItem.title);
        }
    }

    public void onAddItem(View view) {
        aToDoAdapter.add(etEditText.getText().toString());
        TodoItem todoItem = new TodoItem();
        todoItem.title = etEditText.getText().toString();
        etEditText.setText("");
        databaseHelper.addTodoItem(todoItem);
    }

    public void onDeleteAll(View view) {
        databaseHelper.deleteAllTodoItems();
        todoItems.clear();
        aToDoAdapter.notifyDataSetChanged();
    }

    private void launchEditItemView(String todoItem, int itemPosition) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("todoItem", todoItem);
        i.putExtra("todoItemPosition", itemPosition);
        startActivityForResult(i, REQUEST_CODE);
    }

    private TodoItem getDbTodoItemByTitle(String title) {
        for (TodoItem todoItem : DbTodoItems) {
            if (todoItem.title.equals(title)) {
                return todoItem;
            }
        }
        throw new NoSuchElementException("Error while looking for existing todo item.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String originalItem = data.getExtras().getString("originalItem");
            String editedItem = data.getExtras().getString("editedItem");
            int editedItemPosition = data.getExtras().getInt("itemPosition");
            todoItems.set(editedItemPosition, editedItem);
            aToDoAdapter.notifyDataSetChanged();



            TodoItem editedTodoItemFromDb = getDbTodoItemByTitle(originalItem);
            editedTodoItemFromDb.title = editedItem;
            databaseHelper.updateTodoItem(editedTodoItemFromDb);
        }
    }
}
