package com.example.forms.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forms.MainActivity;
import com.example.forms.R;
import com.example.forms.models.Todo;

import java.util.Calendar;
import java.util.List;

/**
 * The adapter class for the RecyclerView, contains the data to render and update.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder>{
    private List<Todo> todos;

    public TodoAdapter(List<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public int getItemCount() {
        return todos.size();
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
        Todo t = todos.get(position);
        Todo todo = MainActivity.dbHandler.getTodoById(t.getId());

        holder.todo_title.setText(todo.getTitle());
        String priority = todo.getPriority().toString();
        holder.todo_priority.setText(priority);

        Button isDoneButton = holder.todo_isDone;
        isDoneButton.setText(todo.getDone());

        Calendar today = Calendar.getInstance();
        Calendar dueDate = todo.getDueDate();
        String dueDateText;

        // Separated for readability
        int year = dueDate.get(Calendar.YEAR);
        int day = dueDate.get(Calendar.DAY_OF_YEAR);
        int todayYear = today.get(Calendar.YEAR);
        int todayDay = today.get(Calendar.DAY_OF_YEAR);

        if (year == todayYear && day == todayDay) {
            dueDateText = String.format("Today @ %s", todo.getTime());
            isDoneButton.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFFF0000);
        } else if (year == todayYear && day == todayDay + 1) {
            dueDateText = String.format("Tomorrow @ %s", todo.getTime());
            isDoneButton.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFDD6600);
        } else if (year < todayYear || (year == todayYear && day < todayDay)) {
            dueDateText = String.format("Overdue: %s @ %s", todo.getDate(), todo.getTime());
            isDoneButton.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFFF0000);
        } else {
            dueDateText = String.format("%s @ %s", todo.getDate(), todo.getTime());
            isDoneButton.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFDD6600);
        }
        holder.todo_due.setText(dueDateText);

        isDoneButton.setOnClickListener(v -> flipUpdateIsDone(isDoneButton, position, todo));

        holder.todoListItemRoot.setOnClickListener(v -> {
            // show dialog with its details
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.todo_detail_dialog);

            TextView title = dialog.findViewById(R.id.todoTitleDialog);
            TextView prio = dialog.findViewById(R.id.todoPriorityDialog);
            TextView due = dialog.findViewById(R.id.todoDueDialog);
            TextView description = dialog.findViewById(R.id.todoDescriptionDialog);
            Button isDone = dialog.findViewById(R.id.todoIsDoneDialog);
            Button delete = dialog.findViewById(R.id.todoDeleteDialog);

            title.setText(todo.getTitle());
            prio.setText(todo.getPriority().toString());
            due.setText(todo.getDateTime());
            description.setText(todo.getDescription());

            updateIsDoneButton(isDone, todo);
            isDone.setOnClickListener(view -> flipUpdateIsDone(isDone, position, todo));

            delete.setOnClickListener(view -> {
                int amountDel = MainActivity.dbHandler.deleteTodo(todo);

                if (amountDel == 0) {
                    Toast.makeText(v.getContext(), "Failed to delete todo", Toast.LENGTH_LONG).show();
                    return;
                }

                // remove from list and notify adapter
                todos.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, this.getItemCount());
                Toast.makeText(v.getContext(), "Todo deleted", Toast.LENGTH_LONG).show();

                dialog.dismiss();
            });

            dialog.setTitle("Todo Details");
            dialog.show();

            // set dialog width to match parent
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        });
    }

    private void flipUpdateIsDone(Button isDone, int position, Todo todo) {
        MainActivity.dbHandler.updateTodo(todo.flipDone());
        updateIsDoneButton(isDone, todo);
        notifyItemChanged(position);
    }

    private void updateIsDoneButton(Button isDone, Todo todo) {
        isDone.setText(todo.getDone());
        isDone.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFFF0000);
    }
}
