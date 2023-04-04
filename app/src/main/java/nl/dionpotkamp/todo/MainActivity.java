package nl.dionpotkamp.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import nl.dionpotkamp.todo.adapters.TodoAdapter;
import nl.dionpotkamp.todo.databinding.ActivityMainBinding;
import nl.dionpotkamp.todo.models.Todo;
import nl.dionpotkamp.todo.utils.DBControl;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    public static DBControl dbControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbControl = new DBControl(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerview = findViewById(R.id.list);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TodoAdapter adapter = (TodoAdapter) recyclerview.getAdapter();

                Todo todo = adapter.getTodoAt(position);
                if (direction == ItemTouchHelper.RIGHT) {
                    todo.delete();
                    adapter.deleteItem(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    Intent intent = new Intent(MainActivity.this, ToDoCreateUpdate.class);
                    intent.putExtra("isUpdate", true);
                    intent.putExtra("id", todo.getId());
                    startActivity(intent);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerview);

        FloatingActionButton fab = binding.fab;
        // OnClick: Create new activity
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, ToDoCreateUpdate.class);
            intent.putExtra("isUpdate", false);
            startActivity(intent);
        });
        // OnLongClick: Show hint
        fab.setOnLongClickListener(view -> {
            Toast.makeText(this, "Create new todo", Toast.LENGTH_SHORT).show();
            refreshList();
            return true;
        });
    }

    private void refreshList() {
        if (dbControl == null) {
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_LONG).show();
            return;
        }

        TodoAdapter adapter = new TodoAdapter(new Todo(-1).getAll());
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