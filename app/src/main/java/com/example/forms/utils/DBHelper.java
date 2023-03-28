package com.example.forms.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// adapted from https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ToDoApp.sqlite";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "todos";

    private static final String ID_COL = "id";
    private static final String TITLE_COL = "title";
    private static final String DUE_COL = "due";
    private static final String DESC_COL = "description";
    private static final String PRIO_COL = "priority";
    private static final String DONE_COL = "isDone";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
