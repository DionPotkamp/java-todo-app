package nl.dionpotkamp.todo.adapters;

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

import nl.dionpotkamp.todo.R;
import nl.dionpotkamp.todo.models.Todo;

import java.util.Calendar;
import java.util.List;

/**
 * The adapter class for the RecyclerView, contains the data to render and update.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    private List<Todo> todos;

    public TodoAdapter(List<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    // get the todo at the specified position
    public Todo getTodoAt(int position) {
        return todos.get(position);
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
        Todo todo = new Todo(t.id);
        todos.set(position, todo);

        holder.title.setText(todo.getTitle());
        String priority = todo.getPriority().toString();
        holder.priority.setText(priority);

        Button isDoneButton = holder.isDone;
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
        holder.dueDate.setText(dueDateText);

        // Set the todo as done or not done
        isDoneButton.setOnClickListener(v -> flipUpdateIsDone(isDoneButton, position, todo));
        // Open dialog when clicking on the item with its details
        holder.rootLayout.setOnClickListener(v -> dialogContent(v, position, todo));
    }

    private void flipUpdateIsDone(Button isDone, int position, Todo todo) {
        int amountUpdated = todo.flipDone().update();

        if (amountUpdated == 0) {
            Toast.makeText(isDone.getContext(), "Failed to update todo", Toast.LENGTH_LONG).show();
            return;
        }

        updateIsDoneButton(isDone, todo);
        notifyItemChanged(position);
    }

    private void updateIsDoneButton(Button isDone, Todo todo) {
        isDone.setText(todo.getDone());
        isDone.setBackgroundColor(todo.isDone() ? 0xFF00FF00 : 0xFFFF0000);
    }

    private void dialogContent(View v, int position, Todo todo) {
        Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.todo_detail_dialog);

        TextView title = dialog.findViewById(R.id.todoTitleDialog);
        TextView prio = dialog.findViewById(R.id.todoPriorityDialog);
        TextView due = dialog.findViewById(R.id.todoDueDialog);
        TextView description = dialog.findViewById(R.id.todoDescriptionDialog);
        Button isDone = dialog.findViewById(R.id.todoIsDoneDialog);

        dialog.findViewById(R.id.closeButton)
                .setOnClickListener(view -> dialog.dismiss());

        title.setText(todo.getTitle());
        prio.setText(todo.getPriority().toString());
        due.setText(todo.getDateTime());
        description.setText(todo.getDescription());

        updateIsDoneButton(isDone, todo);
        isDone.setOnClickListener(view -> flipUpdateIsDone(isDone, position, todo));

        dialog.setTitle("Todo Details");
        dialog.show();

        // Make the dialog full width
        // adapted from https://stackoverflow.com/a/40718796/10463118
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
