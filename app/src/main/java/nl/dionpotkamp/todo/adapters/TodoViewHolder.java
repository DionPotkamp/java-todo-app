package nl.dionpotkamp.todo.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nl.dionpotkamp.todo.R;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class TodoViewHolder extends RecyclerView.ViewHolder {
    // a variable for any view that will be used to render a row
    public TextView title;
    public TextView priority;
    public TextView dueDate;
    public Button isDone;
    public LinearLayout rootLayout;

    // Entire item row, and does the view lookups to find subview
    public TodoViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.todo_title);
        priority = itemView.findViewById(R.id.todo_priority);
        dueDate = itemView.findViewById(R.id.todo_due);
        isDone = itemView.findViewById(R.id.todo_isDone);
        rootLayout = itemView.findViewById(R.id.root_layout);
    }
}
