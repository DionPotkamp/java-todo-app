package nl.dionpotkamp.todo.migrations;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {
    void up(SQLiteDatabase db);
    void down(SQLiteDatabase db);
}
