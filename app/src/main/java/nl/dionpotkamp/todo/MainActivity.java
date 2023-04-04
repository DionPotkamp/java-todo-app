package nl.dionpotkamp.todo;

import android.content.Intent;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new UpdateDeleteSwipe(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
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
                todo.delete();
                adapter.deleteItem(position);
                Toast.makeText(MainActivity.this, "Todo Deleted", Toast.LENGTH_SHORT).show();
            } else if (direction == ItemTouchHelper.LEFT) {
                Intent intent = new Intent(MainActivity.this, ToDoCreateUpdate.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("id", todo.getId());
                startActivity(intent);
            }
        }

        // adapted from https://stackoverflow.com/a/33344173/10463118
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
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