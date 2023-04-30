package nl.dionpotkamp.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import nl.dionpotkamp.todo.adapters.TodoAdapter;
import nl.dionpotkamp.todo.databinding.ActivityMainBinding;
import nl.dionpotkamp.todo.enums.SortDirection;
import nl.dionpotkamp.todo.models.Todo;
import nl.dionpotkamp.todo.utils.DBControl;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityMainBinding binding;
    RecyclerView recyclerview;
    SwipeRefreshLayout swipeRefreshLayout;
    public static DBControl dbControl;
    TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbControl = new DBControl(this);

        recyclerview = binding.list;
        swipeRefreshLayout = binding.swipeContainer;
        swipeRefreshLayout.setOnRefreshListener(this);

        // Add swipe to delete and update
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new UpdateDeleteSwipe(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        itemTouchHelper.attachToRecyclerView(recyclerview);

        // On click: create new activity
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, TodoCreateActivity.class);
            startActivity(intent);
        });
        // On long click: show hint
        binding.fab.setOnLongClickListener(view -> {
            Toast.makeText(this, binding.fab.getContentDescription(), Toast.LENGTH_SHORT).show();
            return true;
        });

        // OnClick: Sort list by dateSort
        binding.fabDate.setOnClickListener(view -> {
            TodoAdapter.flipSort();
            refreshList();
        });
    }

    private void refreshList() {
        if (dbControl == null) {
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Refresh to retry", Toast.LENGTH_LONG).show();
            return;
        }

        if (todoAdapter == null)
            todoAdapter = new TodoAdapter(this);

        todoAdapter.loadTodos();
        recyclerview.setAdapter(todoAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("todo.sort", MODE_PRIVATE);
        TodoAdapter.dateSort = SortDirection.valueOf(prefs.getString("dateSort", "ASC"));

        refreshList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences("todo.sort", MODE_PRIVATE).edit();
        editor.putString("dateSort", TodoAdapter.dateSort.name());
        editor.apply();
    }

    @Override
    public void onRefresh() {
        refreshList();
        swipeRefreshLayout.setRefreshing(false);
    }

    // UpdateDeleteSwipe.onChildDraw adapted from https://stackoverflow.com/a/33344173/10463118
    class UpdateDeleteSwipe extends ItemTouchHelper.SimpleCallback {
        public UpdateDeleteSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TodoAdapter adapter = (TodoAdapter) recyclerview.getAdapter();

            if (adapter == null)
                return;

            Todo todo = adapter.getTodoAt(position);
            if (direction == ItemTouchHelper.RIGHT) {
                if (!todo.delete()) {
                    Toast.makeText(MainActivity.this, "Could not delete todo", Toast.LENGTH_LONG).show();
                    return;
                }
                refreshList();

                Snackbar.make(recyclerview, "Todo deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            // It is still stored in the variable.
                            // We can just save it again, id is set to -1 so it will be inserted as new
                            todo.save();
                            refreshList();
                        })
                        .show();
            } else if (direction == ItemTouchHelper.LEFT) {
                Intent intent = new Intent(MainActivity.this, TodoUpdateActivity.class);
                intent.putExtra("id", todo.getId());
                startActivity(intent);
            }
        }

        // adapted from https://stackoverflow.com/a/33344173/10463118
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                Paint p = new Paint();

                if (dX > 0) { // Swiping to the right (delete)
                    // Set background color to red
                    p.setARGB(155, 255, 0, 0);
                    // Draw background
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                            (float) itemView.getBottom(), p);
                    // Draw delete icon
                    c.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete), (float) itemView.getLeft() + 20, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - 100) / 2, null);
                } else if (dX < 0) { // Swiping to the left (update)
                    // Set background color to orange
                    p.setARGB(155, 255, 165, 0);
                    // Draw background
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    // Draw edit icon
                    c.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_edit), (float) itemView.getRight() - 150, (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - 100) / 2, null);
                }

                // Fade out the view as it is swiped out of the parent's bounds
                final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    }
}
