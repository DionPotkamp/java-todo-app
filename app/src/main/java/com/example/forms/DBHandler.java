package com.example.forms;

import static com.example.forms.MainActivity.todos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.forms.models.Todo;
import com.example.forms.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

// adapted from https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/
public class DBHandler extends SQLiteOpenHelper {
    // dictionary of columns
//    private static final Map<String, String> ToDoApp = new HashMap<String, String>() {
//        {
//            put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
//            put("title", "TEXT");
//            put("due", "TEXT");
//            put("description", "TEXT");
//            put("priority", "INTEGER");
//            put("isDone", "INTEGER");
//        }
//    };

    private static final String DB_NAME = "ToDoApp";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "todos";

    private static final String ID_COL = "id";
    private static final String TITLE_COL = "title";
    private static final String DUE_COL = "due";
    private static final String DESC_COL = "description";
    private static final String PRIO_COL = "priority";
    private static final String DONE_COL = "isDone";
    private static final String[] ALL_COLS = {"*"};

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public Todo addNewTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // if it already exists, update
        if (todo.getId() != -1) {
            return updateTodo(todo);
        }

        ContentValues values = makeContentValuesFromTodo(todo);
        long id = db.insert(TABLE_NAME, null, values);
        todo.setId((int) id);
        db.close();

        return todo;
    }

    public Todo updateTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // if it doesn't exist, add
        int id = todo.getId();
        if (id == -1) {
            return addNewTodo(todo);
        }

        ContentValues values = makeContentValuesFromTodo(todo);
        db.update(TABLE_NAME, values, ID_COL + "=?", new String[] { String.valueOf(id) });
        db.close();

        return todo;
    }

    public List<Todo> getAllTodos() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.query( TABLE_NAME, ALL_COLS, null, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            todos.add(makeTodoFromCursor(cursor));
            cursor.moveToNext();
        }

        db.close();
        return todos;
    }

    public Todo getTodoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.query(TABLE_NAME, ALL_COLS, ID_COL + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        db.close();
        return makeTodoFromCursor(cursor);
    }

    public int deleteTodo(Todo todo) {
        return deleteTodoById(todo.getId());
    }

    public int deleteTodoById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.delete(TABLE_NAME, ID_COL + "=?", new String[] { String.valueOf(id) });

        db.close();
        return rowsAffected;
    }

    private ContentValues makeContentValuesFromTodo(Todo todo) {
        ContentValues values = new ContentValues();
        values.put(TITLE_COL, todo.getTitle());
        values.put(DESC_COL, todo.getDescription());
        values.put(DUE_COL, DateHelper.stringFromCalendar(todo.getDueDate()));
        values.put(PRIO_COL, todo.getPriority().getValue());
        values.put(DONE_COL, todo.isDone());

        return values;
    }

    private Todo makeTodoFromCursor(Cursor cursor) {
        return new Todo(
                cursor.getInt(0), // id
                cursor.getString(1), // title
                cursor.getString(3), // description
                DateHelper.calendarFromString(cursor.getString(2)), // due date
                Todo.Priority.values()[cursor.getInt(4)], // priority
                (cursor.getInt(5) == 1) // isDone
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_COL + " TEXT,"
                + DUE_COL + " TEXT,"
                + DESC_COL + " TEXT,"
                + PRIO_COL + " INTEGER,"
                + DONE_COL + " INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
