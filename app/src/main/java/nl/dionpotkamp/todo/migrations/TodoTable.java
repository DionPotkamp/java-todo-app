package nl.dionpotkamp.todo.migrations;

import android.database.sqlite.SQLiteDatabase;

public class TodoTable implements Migration {
    public static final String TABLE_NAME = "todos";
    public static final String[] COLUMN_NAMES = new String[]{"id", "title", "due", "description", "priority", "isDone"};

    @Override
    public void up(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, due INTEGER, description TEXT, priority INTEGER, isDone INTEGER)");
    }

    @Override
    public void down(SQLiteDatabase db) {
        db.execSQL("DROP TABLE todos");
    }

    @Override
    public String getTableName() {
        return TodoTable.TABLE_NAME;
    }

    @Override
    public String[] getColumnNames() {
        return TodoTable.COLUMN_NAMES;
    }
}
