package nl.dionpotkamp.todo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database control class.
 * Contains methods to insert, update, delete and select data from database.
 * Does not call close() on database.
 * When close() is called it throws an exception:
 * java.lang.IllegalStateException: Cannot perform this operation because the connection pool has been closed.
 *
 * @author Dion Potkamp
 */
public class DBControl extends DBHelper {
    public DBControl(Context context) {
        super(context);
    }

    /**
     * Insert data into database.
     *
     * @param table  Table name
     * @param values Values to insert
     * @return Inserted row id
     */
    public int insert(String table, ContentValues values) {
        if (table == null)
            throw new RuntimeException("Table not set");
        if (values == null || values.size() == 0)
            throw new RuntimeException("Values not set");

        SQLiteDatabase db = this.getWritableDatabase();

        return (int) db.insert(table, null, values);
    }

    /**
     * Update data in database.
     *
     * @param table       Table name
     * @param values      Values to update
     * @param whereClause Where clause. Use "?" for values
     * @param whereArgs   Where clause arguments
     * @return Number of updated rows
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (table == null)
            throw new RuntimeException("Table not set");
        if (values == null || values.size() == 0)
            throw new RuntimeException("Values not set");

        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(table, values, whereClause, whereArgs);
    }

    /**
     * Delete data from database.
     *
     * @param table       Table name
     * @param whereClause Where clause. Use "?" for values
     * @param whereArgs   Where clause arguments
     * @return Number of deleted rows
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        if (table == null)
            throw new RuntimeException("Table not set");

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(table, whereClause, whereArgs);
    }

    /**
     * Select data from database.
     * Call cursor.close() after use.
     *
     * @param table         Table name
     * @param columns       Columns to select
     * @param selection     Where clause. Use "?" for values
     * @param selectionArgs Where clause arguments
     * @param groupBy       Group by
     * @param having        Having
     * @param orderBy       Order by
     * @return Cursor
     */
    public Cursor select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (table == null)
            throw new RuntimeException("Table not set");

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}
