package com.example.lilo.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lilo.todoapp.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoItemDatabase extends SQLiteOpenHelper {
    private static TodoItemDatabase sInstance;

    private static final String TAG = TodoItemDatabase.class.getName();

    // Database Info
    private static final String DATABASE_NAME = "todoListDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODO_ITEMS = "todo_items";

    // Todo Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";

    public static synchronized TodoItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE" + TABLE_TODO_ITEMS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TITLE + " TEXT" +
                ")";
        db.execSQL(CREATE_TODO_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_ITEMS);
            onCreate(db);
        }
    }

    public void addTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, todoItem.title);

            db.insertOrThrow(TABLE_TODO_ITEMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while adding todo item to database");
        } finally {
            db.endTransaction();
        }
    }

    public long updateTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        long todoItemId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, todoItem.title);

            int rows = db.update(
                    TABLE_TODO_ITEMS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(todoItem.getId())}
            );

            if (rows == 1) {
                String todoItemSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_ID, TABLE_TODO_ITEMS, KEY_TITLE);
                Cursor cursor = db.rawQuery(todoItemSelectQuery,
                        new String[]{String.valueOf(todoItem.getId())});
                try {
                    if (cursor.moveToFirst()) {
                        todoItemId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // todoItem with this title did not already exist, so insert new todoItem
                todoItemId = db.insertOrThrow(TABLE_TODO_ITEMS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update todo item");
        } finally {
            db.endTransaction();
        }
        return todoItemId;
    }

    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();

        String TODO_ITEMS_SELECT_QUERY = String.format("SELECT  * FROM " + TABLE_TODO_ITEMS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                TodoItem newTodoItem = new TodoItem();
                newTodoItem.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                todoItems.add(newTodoItem);
            }
            while (cursor.moveToNext()) ;
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get todo items from database");
        } finally {
            if (cursor != null && cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItems;
    }

    public void deleteTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO_ITEMS, KEY_ID + " =?", new String[] {String.valueOf(todoItem.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete todo item from database");
        } finally {
            db.endTransaction();
        }
    }
}
