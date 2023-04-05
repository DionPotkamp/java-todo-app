package nl.dionpotkamp.todo.migrations;

import android.database.sqlite.SQLiteDatabase;

public class TodoTable implements Migration {
    @Override
    public void up(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, due INTEGER, description TEXT, priority INTEGER, isDone INTEGER)");
    }

    @Override
    public void down(SQLiteDatabase db) {
        db.execSQL("DROP TABLE todos");
    }
}
