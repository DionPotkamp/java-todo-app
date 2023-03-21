package com.example.forms.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.forms.R;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class TodoViewHolder extends RecyclerView.ViewHolder {
    // a variable for any view that will be used to render a row
    public TextView todo_title;
    public TextView todo_priority;
    public TextView todo_due;
    public Button todo_isDone;
    public LinearLayout todoListItemRoot;

    // Entire item row, and does the view lookups to find subview
    public TodoViewHolder(View itemView) {
        super(itemView);

        todo_title = itemView.findViewById(R.id.todo_title);
        todo_priority = itemView.findViewById(R.id.todo_priority);
        todo_due = itemView.findViewById(R.id.todo_due);
        todo_isDone = itemView.findViewById(R.id.todo_isDone);
        todoListItemRoot = itemView.findViewById(R.id.todoListItemRoot);
    }
}
