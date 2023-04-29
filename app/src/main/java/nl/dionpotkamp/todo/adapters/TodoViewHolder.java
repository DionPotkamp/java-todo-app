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
    public TextView title;
    public TextView priority;
    public TextView dueDate;
    public Button isDone;
    public LinearLayout rootLayout;

    public TodoViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.todo_title);
        priority = itemView.findViewById(R.id.todo_priority);
        dueDate = itemView.findViewById(R.id.todo_due);
        isDone = itemView.findViewById(R.id.todo_isDone);
        rootLayout = itemView.findViewById(R.id.todo_root_layout);
    }
}
