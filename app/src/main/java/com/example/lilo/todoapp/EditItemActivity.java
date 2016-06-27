package com.example.lilo.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    EditText etItemToEdit;
    int todoItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String todoItem = getIntent().getStringExtra("todoItemTitle");
        todoItemPosition = getIntent().getIntExtra("todoItemPosition", 0);
        etItemToEdit = (EditText) findViewById(R.id.etItemToEdit);
        etItemToEdit.setText(todoItem);
        etItemToEdit.setSelection(todoItem.length());
    }

    public void onSaveItem(View view) {
        Intent data = new Intent();
        data.putExtra("editedItem", etItemToEdit.getText().toString());
        data.putExtra("itemPosition", todoItemPosition);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
