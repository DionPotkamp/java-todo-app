package nl.dionpotkamp.todo.models;

import static nl.dionpotkamp.todo.MainActivity.dbControl;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import nl.dionpotkamp.todo.migrations.Migration;

/**
 * Model class for all models, all models must extend this class.
 * This class contains methods to get all models of a type.
 *      For this to work, the child must have an empty constructor.
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
    protected int id = -1;
    public int getId() {
        return id;
    }

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
    @NonNull
    public abstract Model clone();

    /**
     * @param migration Migration to get table and columns from
     */
    public Model(Migration migration) {
        // Setting table and columns could be done in constructor of child class
        // But this way it's enforced to set the table and columns
        dbTable = migration.getTableName();
        dbColumns = migration.getColumnNames();
    }

    /**
     * Create a new row of model in database.
     * Sets id of model to id of created row.
     */
    public void create() {
        // Remove id from values, because it is auto incremented
        ContentValues values = getContentValues();
        values.remove("id");

        id = dbControl.insert(dbTable, values);
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
    public boolean delete() {
        ContentValues values = getContentValues();
        String id = values.getAsString("id");

        int amountDeleted = dbControl.delete(dbTable, "id=?", new String[]{id});
        if (amountDeleted > 1) // Should never happen
            System.err.printf("More than one (%d) row deleted using ID: %s", amountDeleted, id);

        boolean deleted = amountDeleted > 0;
        // Set id to -1 if model is deleted, so it can be saved again
        if (deleted)
            this.id = -1;

        return deleted;
    }

    /**
     * Save model in database.
     * Decides if it should create or update the model.
     *
     * @return Object type E which extends Model saved in database
     */
    public <E extends Model> E save() {
        if (id == -1)
            create();
        else
            update();

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
     * Get current model from database and set the values of this model.
     */
    public void get() {
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = dbControl.select(dbTable, dbColumns, "id=?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            setValuesFromCursor(cursor);
        }

        cursor.close();
    }

    /**
     * Get all models of type E which extends Model.
     * If no records are found, an empty list will be returned.
     *
     * @param modelClass Class of model
     * @return List with all models of type E which extends Model
     */
    public static <E extends Model> List<E> getAll(Class<E> modelClass) {
        List<E> list = new ArrayList<>();

        E model = createClassFromName(modelClass);
        if (model == null)
            return list;

        Cursor cursor = dbControl.select(model.dbTable, model.dbColumns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            E newModel = createClassFromName(modelClass);
            if (newModel == null)
                continue;

            newModel.setValuesFromCursor(cursor);
            list.add(newModel);
        }

        cursor.close();
        return list;
    }

    private static <E extends Model> E createClassFromName(Class<E> modelClass) {
        E model;

        try {
            model = modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.printf("Could not create new instance of model %s. %s", modelClass.getName(), e.getMessage());
            return null;
        }

        return model;
    }
}
