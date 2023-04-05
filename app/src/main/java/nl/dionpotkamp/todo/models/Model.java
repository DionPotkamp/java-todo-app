package nl.dionpotkamp.todo.models;

import static nl.dionpotkamp.todo.MainActivity.dbControl;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for all models, all models must extend this class.
 * This class contains methods to get all models of a type.
 * When implemented correctly, this class can be used to get all models of a type.
 *
 * @author Dion Potkamp
 */
public abstract class Model implements Cloneable {
    /**
     * The table name in the database.
     */
    public final String dbTable;
    /**
     * The column names in the database.
     */
    public final String[] dbColumns;
    /**
     * The id of the model, the primary key in the database.
     * -1 means the model is not saved in the database.
     */
    public int id = -1;

    /**
     * @return ContentValues with all values of this model
     */
    public abstract ContentValues getContentValues();

    /**
     * Set values of this model from cursor.
     *
     * @param cursor Cursor with data
     */
    public abstract void setValuesFromCursor(Cursor cursor);

    /**
     * @return Clone of this model
     */
    public abstract Model clone();

    /**
     * @param table Name of table in database
     * @param columns Names of columns in database
     */
    public Model(String table, String[] columns) {
        // Setting table and columns could be done in constructor of child class
        // But this way it's enforced to set the table and columns
        dbTable = table;
        dbColumns = columns;
    }

    /**
     * Create a new row of model in database.
     * Sets id of model to id of created row.
     *
     * @return id of created row
     */
    public int create() {
        // Remove id from values, because id is auto incremented
        ContentValues values = getContentValues();
        values.remove("id");

        id = dbControl.insert(dbTable, values);
        return id;
    }

    /**
     * Update current model in database.
     *
     * @return Number of updated rows
     */
    public int update() {
        ContentValues values = getContentValues();
        String[] whereArgs = new String[]{values.getAsString("id")};
        // Remove id from values, because it cannot be updated
        values.remove("id");

        return dbControl.update(dbTable, values, "id=?", whereArgs);
    }

    /**
     * Delete current model from database.
     *
     * @return Number of deleted rows
     */
    public int delete() {
        ContentValues values = getContentValues();
        String id = values.getAsString("id");

        return dbControl.delete(dbTable, "id=?", new String[]{id});
    }

    /**
     * Save model in database.
     * Decides if it should create or update the model.
     *
     * @return Object type E which extends Model saved in database
     */
    public <E extends Model> E save() {
        if (id == -1) {
            create();
        } else {
            update();
        }

        return (E) this;
    }

    /**
     * Get cursor for current model from database.
     * WARNING: Call cursor.close() after using this method.
     *
     * @return Cursor with all requested rows
     */
    public Cursor read() {
        String[] selectionArgs = new String[]{String.valueOf(id)};
        return dbControl.select(dbTable, dbColumns, "id=?", selectionArgs, null, null, null);
    }

    /**
     * Get current model from database and set values of this model.
     *
     * @return Object type E which extends Model
     */
    public <E extends Model> E get() {
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = dbControl.select(dbTable, dbColumns, "id=?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            setValuesFromCursor(cursor);
        }

        cursor.close();

        return (E) this;
    }

    /**
     * Get all models of type E which extends Model.
     * If no records are found, an empty list will be returned.
     *
     * @return List with all models of type E which extends Model
     */
    public <E extends Model> List<E> getAll() {
        Cursor cursor = dbControl.select(dbTable, dbColumns, null, null, null, null, null);

        List<E> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            this.setValuesFromCursor(cursor);
            list.add((E) this.clone());
        }

        cursor.close();

        return list;
    }
}
