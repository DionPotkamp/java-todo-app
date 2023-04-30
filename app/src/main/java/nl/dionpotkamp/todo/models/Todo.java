package nl.dionpotkamp.todo.models;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.util.Calendar;

import nl.dionpotkamp.todo.enums.Priority;
import nl.dionpotkamp.todo.migrations.TodoTable;
import nl.dionpotkamp.todo.utils.DateHelper;

public class Todo extends Model {
    private String title;
    private Calendar dueDate;
    private String description;
    private Priority priority;
    private boolean isDone;

    /**
     * Creates a new object with -1 as id.
     * Used for creating a new object in Model class
     */
    public Todo() {
        this(-1, null, null, null, null, false);
    }

    /**
     * Creates a new object with the given id.
     * Automatically calls get() to get the data from the database.
     */
    public Todo(int id) {
        this(id, null, null, null, null, false);
        if (id != -1)
            get();
    }

    /**
     * Creates a new Todo object with the given properties.
     *
     * @param id          the id of the todo
     * @param title       the title of the todo
     * @param dueDate     the due date of the todo
     * @param description the description of the todo
     * @param priority    the priority of the todo
     * @param isDone      whether the todo is done or not
     */
    public Todo(int id, String title, Calendar dueDate, String description, Priority priority, boolean isDone) {
        super(new TodoTable());

        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.priority = priority;
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public String getDate() {
        int month = dueDate.get(Calendar.MONTH) + 1; // months start at 0
        int day = dueDate.get(Calendar.DAY_OF_MONTH);
        int year = dueDate.get(Calendar.YEAR);

        return String.format("%d/%d/%d", month, day, year);
    }

    public String getTime() {
        int hour = dueDate.get(Calendar.HOUR_OF_DAY);
        int minute = dueDate.get(Calendar.MINUTE);

        // Add a 0 to the left if needed
        return String.format("%02d:%02d", hour, minute);
    }

    public String getDateTime() {
        return String.format("%s %s", getDate(), getTime());
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getDone() {
        return isDone ? "Done" : "Not Done";
    }

    public boolean isDone() {
        return isDone;
    }

    public Todo flipDone() {
        isDone = !isDone;
        return this;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("title", title);
        values.put("due", DateHelper.stringFromCalendar(dueDate));
        values.put("description", description);
        values.put("priority", priority.toString());
        values.put("isDone", isDone ? 1 : 0);
        return values;
    }

    @Override
    public void setValuesFromCursor(Cursor cursor) {
        if (cursor == null) return;
        if (cursor.getCount() == 0) return;
        if (cursor.getColumnCount() != dbColumns.length) return;
        if (cursor.isAfterLast()) return;
        if (cursor.isBeforeFirst()) cursor.moveToFirst();

        id = cursor.getInt(0);
        title = cursor.getString(1);
        dueDate = DateHelper.calendarFromString(cursor.getString(2));
        description = cursor.getString(3);
        priority = Priority.valueOf(cursor.getString(4));
        isDone = cursor.getInt(5) == 1;
    }

    @NonNull
    @Override
    public Todo clone() {
        return new Todo(id, title, dueDate, description, priority, isDone);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("ID: %s Title: '%s' Due Date: '%s' Description: '%s' Priority: '%s' isDone: '%s'", id, title, getDateTime(), description.replace("\n", "\\n"), priority, isDone);
    }
}
