package com.example.forms.models;

import com.example.forms.utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Todo {
    private int id = -1;
    private String title = "";
    private String description = "";
    private Calendar dueDate = Calendar.getInstance();
    private Priority priority = Priority.High;
    private boolean isDone = false;

    public enum Priority {
        High(0),
        Medium(1),
        Low(2);

        private final int value;
        Priority(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }

    public Todo(String title, String description, Calendar dueDate, Priority priority, boolean isDone) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isDone = isDone;
    }

    public Todo(int id, String title, String description, Calendar dueDate, Priority priority, boolean isDone) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isDone = isDone;
    }

    public static List<Todo> createTodosList() {
        List<Todo> todos = new ArrayList<>();

        Calendar date = Calendar.getInstance();
        date.set(2022, 1, 1, 12, 0);
        todos.add(new Todo(1, "Todo 1wertyuiopiuytrewertyuioiuytrew", "Description 1", date, Priority.High, false));
        date = Calendar.getInstance();
        date.set(2023, 3, 14, 23, 0);
        todos.add(new Todo(2, "Todo 2", "Description 2", date, Priority.Medium, false));
        date = Calendar.getInstance();
        date.set(2023, 3, 15, 12, 0);
        todos.add(new Todo(3, "Todo 3", "Description 3", date, Priority.Low, false));
        date = Calendar.getInstance();
        date.set(2024, 1, 3, 12, 0);
        todos.add(new Todo(3, "Todo 3", "Description 3", date, Priority.Low, false));

        return todos;
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

    public String getDescription() {
        return description;
    }

    public Todo setDescription(String description) {
        this.description = description;
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
        int day = dueDate.get(Calendar.DAY_OF_MONTH);
        int month = dueDate.get(Calendar.MONTH) + 1;
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
    public String toString() {
        return String.format("Title: '%s' Description: '%s' Due Date: '%s' Priority: '%s' isDone: '%s'", title, description.replace("\n", "\\n"), getDateTime(), priority, isDone);
    }
}
