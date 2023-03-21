package com.example.forms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.forms.adapters.TodoAdapter;
import com.example.forms.models.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forms.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {

    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    public static DBHandler dbHandler;
    public static List<Todo> todos = new ArrayList<>();
    public static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerview = findViewById(R.id.list);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);

        refreshList();

        FloatingActionButton fab = binding.fab;
        // OnClick: Create new activity
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, ToDoCreateUpdate.class);
            startActivity(intent);
        });
        // OnLongClick: Show hint and create new todos in debug mode
        fab.setOnLongClickListener(view -> {
            Toast.makeText(this, "Create new todo", Toast.LENGTH_SHORT).show();
            if(MainActivity.DEBUG) todos.addAll(Todo.createTodosList());
            refreshList();
            return true;
        });
    }

    private void refreshList() {
        if (dbHandler == null) dbHandler = new DBHandler(this);

        todos.clear();
        todos = dbHandler.getAllTodos();

        TodoAdapter adapter = new TodoAdapter(todos);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }
}