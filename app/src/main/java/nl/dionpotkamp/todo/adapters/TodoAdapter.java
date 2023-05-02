package nl.dionpotkamp.todo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import nl.dionpotkamp.todo.R;
import nl.dionpotkamp.todo.TodoDetailActivity;
import nl.dionpotkamp.todo.enums.SortDirection;
import nl.dionpotkamp.todo.migrations.TodoTable;
import nl.dionpotkamp.todo.models.Model;
import nl.dionpotkamp.todo.models.Todo;

/**
 * The adapter class for the RecyclerView, contains the data to render and update.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    private List<Todo> todos;
    public static SortDirection dateSort;
    public static int GreenColor;
    public static int RedColor;
    public static int OrangeColor;

    public TodoAdapter(Context context) {
        GreenColor = context.getResources().getColor(R.color.color_brand, context.getTheme());
        RedColor = context.getResources().getColor(R.color.color_red, context.getTheme());
        OrangeColor = context.getResources().getColor(R.color.color_orange, context.getTheme());
    }

    public void loadTodos() {
        todos = Model.getAll(Todo.class, TodoTable.COLUMN_NAMES[2], dateSort);
        notifyDataSetChanged();
    }

    /**
     * The data to render and update.
     */
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout. Inflating a layout means to render the layout as a View object.
        View todoView = inflater.inflate(R.layout.todo_list_item, parent, false);

        // Return a new holder instance
        return new TodoViewHolder(todoView);
    }

    /**
     * The ViewHolder describes an item view and data about its place within the RecyclerView.
     * Here is the data set to the view.
     */
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        // Get the data model based on position & make sure we have the latest data
        Todo _todo = todos.get(position);
        Todo todo = new Todo(_todo.getId());
        todos.set(position, todo);

        holder.title.setText(todo.getTitle());
        String priority = todo.getPriority().toString();
        holder.priority.setText(priority);

        Button isDoneButton = holder.isDone;
        isDoneButton.setText(todo.getDone());

        Calendar today = Calendar.getInstance();
        Calendar dueDate = todo.getDueDate();
        String dueDateText;
        int backgroundColor;

        // Separated for readability
        int year = dueDate.get(Calendar.YEAR);
        int day = dueDate.get(Calendar.DAY_OF_YEAR);
        int todayYear = today.get(Calendar.YEAR);
        int todayDay = today.get(Calendar.DAY_OF_YEAR);
        String date = todo.getDate();
        String time = todo.getTime();
        boolean isDone = todo.isDone();

        if (year == todayYear && day == todayDay) {
            dueDateText = String.format("Today @ %s", time);
            backgroundColor = isDone ? GreenColor : RedColor;
        } else if (year == todayYear && day == todayDay + 1) {
            dueDateText = String.format("Tomorrow @ %s", time);
            backgroundColor = isDone ? GreenColor : OrangeColor;
        } else if (year < todayYear || (year == todayYear && day < todayDay)) {
            dueDateText = String.format("Overdue: %s @ %s", date, time);
            backgroundColor = isDone ? GreenColor : RedColor;
        } else {
            dueDateText = String.format("%s @ %s", date, time);
            backgroundColor = isDone ? GreenColor : OrangeColor;
        }
        holder.dueDate.setText(dueDateText);
        isDoneButton.setBackgroundColor(backgroundColor);

        // Set as done or not done
        isDoneButton.setOnClickListener(v -> flipUpdateIsDone(isDoneButton, position, todo));
        holder.rootLayout.setOnClickListener(v -> openDetailActivity(v, todo));
    }

    private void openDetailActivity(View v, Todo todo) {
        Intent intent = new Intent(v.getContext(), TodoDetailActivity.class);
        intent.putExtra("id", todo.getId());
        v.getContext().startActivity(intent);
    }

    public void flipUpdateIsDone(Button isDone, int position, Todo todo) {
        if (todo.flipDone().update() == 0) {
            Toast.makeText(isDone.getContext(), "Failed to update todo", Toast.LENGTH_LONG).show();
        } else {
            isDone.setText(todo.getDone());
            isDone.setBackgroundColor(todo.isDone() ? GreenColor : 0xFFFF0000);
            notifyItemChanged(position);
        }
    }

    public static void flipSort() {
        dateSort = dateSort == SortDirection.ASC ? SortDirection.DESC : SortDirection.ASC;
    }

    public Todo getTodoAt(int position) {
        return todos.get(position);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }
}
