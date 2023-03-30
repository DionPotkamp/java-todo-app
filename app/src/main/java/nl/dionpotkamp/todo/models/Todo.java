package nl.dionpotkamp.todo.models;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import nl.dionpotkamp.todo.enums.Priority;
import nl.dionpotkamp.todo.utils.DateHelper;

import java.util.Calendar;

public class Todo extends Model {
    private String title;
    private Calendar dueDate;
    private String description;
    private Priority priority;
    private boolean isDone;

    /**
     * Creates a new Todo object with the given id.
     * Automatically calls get() to get the data from the database.
     */
    public Todo(int id) {
        this(id, "", Calendar.getInstance(), "", Priority.Low, false);
        if (id != -1)
            get();
    }

    /**
     * Creates a new Todo object with the given properties.
     * id is set to -1 initially and will be set by calling save().
     */
    public Todo(String title, Calendar dueDate, String description, Priority priority, boolean isDone) {
        this(-1, title, dueDate, description, priority, isDone);
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
        super("todos", new String[]{"id", "title", "due", "description", "priority", "isDone"});

        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.priority = priority;
        this.isDone = isDone;
    }

    public int getId() {
        return id;
    }

    public Todo setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Todo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public Todo setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
        return this;
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

        return String.format("%02d:%02d", hour, minute); // ads 0 to the left if needed
    }

    public String getDateTime() {
        return String.format("%s %s", getDate(), getTime());
    }

    public String getDescription() {
        return description;
    }

    public Todo setDescription(String description) {
        this.description = description;
        return this;
    }

    public Priority getPriority() {
        return priority;
    }

    public Todo setPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public String getDone() {
        return isDone ? "Done" : "Not Done";
    }

    public boolean isDone() {
        return isDone;
    }

    public Todo setDone(boolean done) {
        isDone = done;
        return this;
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
