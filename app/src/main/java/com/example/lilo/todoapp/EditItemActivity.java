package com.example.lilo.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    EditText etItemToEdit;
    int todoItemPosition;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String todoItem = getIntent().getStringExtra("todoItem");
        todoItemPosition = getIntent().getIntExtra("todoItemPosition", 0);
        etItemToEdit = (EditText) findViewById(R.id.etItemToEdit);
        etItemToEdit.setText(todoItem);
        etItemToEdit.setSelection(todoItem.length());

        data = new Intent();
        data.putExtra("originalItem", etItemToEdit.getText().toString());
    }

    public void onSaveItem(View view) {
        data.putExtra("editedItem", etItemToEdit.getText().toString());
        data.putExtra("itemPosition", todoItemPosition);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
