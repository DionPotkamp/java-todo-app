package nl.dionpotkamp.todo;

import static nl.dionpotkamp.todo.adapters.TodoAdapter.GreenColor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import nl.dionpotkamp.todo.databinding.ActivityTodoDetailBinding;
import nl.dionpotkamp.todo.databinding.ContentScrollingBinding;
import nl.dionpotkamp.todo.models.Todo;

public class TodoDetailActivity extends AppCompatActivity {
    private ActivityTodoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTodoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        // Show back button in toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Todo todo = new Todo(getIntent().getIntExtra("id", -1));

        if (todo.getId() == -1) {
            Toast.makeText(this, "Todo not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initView(todo);
        binding.fab.setOnClickListener(view -> editTodo(view, todo));
    }

    private void initView(Todo todo) {
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(todo.getTitle());

        ContentScrollingBinding binding = this.binding.detailContentScrolling;

        TextView priority = binding.detailPriority;
        TextView due = binding.detailDue;
        TextView description = binding.detailDescription;
        Button isDone = binding.detailIsDoneButton;

        priority.setText(todo.getPriority().toString());
        due.setText(todo.getDateTime());
        description.setText(todo.getDescription());

        updateIsDoneButton(isDone, todo);
        isDone.setOnClickListener(view -> flipDone(todo, isDone));
    }

    private void flipDone(Todo todo, Button isDone) {
        if (todo.flipDone().update() == 0) {
            Toast.makeText(isDone.getContext(), "Failed to update todo", Toast.LENGTH_LONG).show();
        } else {
            updateIsDoneButton(isDone, todo);
        }
    }

    private void editTodo(View view, Todo todo) {
        Intent intent = new Intent(view.getContext(), TodoUpdateActivity.class);
        intent.putExtra("id", todo.getId());
        startActivity(intent);
    }

    private void updateIsDoneButton(Button isDone, Todo todo) {
        isDone.setText(todo.getDone());
        isDone.setBackgroundColor(todo.isDone() ? GreenColor : 0xFFFF0000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Todo todo = new Todo(getIntent().getIntExtra("id", -1));

        if (todo.getId() == -1) {
            Toast.makeText(this, "Todo not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initView(todo);
    }
}