package nl.dionpotkamp.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Objects;

import nl.dionpotkamp.todo.databinding.ActivityTodoCreateUpdateBinding;
import nl.dionpotkamp.todo.enums.Priority;
import nl.dionpotkamp.todo.models.Todo;

public class TodoCreateActivity extends AppCompatActivity {

    ActivityTodoCreateUpdateBinding binding;

    private Button saveButton;
    private EditText titleText, descriptionText;
    private SwitchCompat isDoneSwitch;

    // Calendar variables
    boolean dateSet, timeSet = false;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding = ActivityTodoCreateUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView title = binding.createUpdateTitle;
        title.setText(R.string.create_todo);

        titleText = binding.editTextTitle;
        isDoneSwitch = binding.isDoneSwitch;
        descriptionText = binding.editTextDescription;

        // Adapted from https://stackoverflow.com/a/17650125/10463118
        // values from enum are used to populate the spinner/ dropdown
        ArrayAdapter<Priority> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Priority.values());

        binding.prioritySpinner.setAdapter(adapter);
        // High as default
        binding.prioritySpinner.setSelection(0);

        saveButton = binding.saveButton;
        binding.saveButton.setText(R.string.add_todo);
        binding.saveButton.setOnClickListener(v -> saveTodo());

        binding.backButton.setText(R.string.todo_back);
        binding.backButton.setOnClickListener(v -> finish());

        // Adapted from https://www.digitalocean.com/community/tutorials/android-date-time-picker-dialog
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        binding.dateButton.setOnClickListener(v ->
                new DatePickerDialog(
                        this,
                        this::onDateSetListener,
                        mYear, mMonth, mDay
                ).show()
        );
        binding.timeButton.setOnClickListener(v ->
                new TimePickerDialog(
                        this,
                        this::onTimeSetListener,
                        mHour, mMinute, true
                ).show()
        );
    }

    private void onDateSetListener(View view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        dateSet = true;
    }

    private void onTimeSetListener(View view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        timeSet = true;
    }

    private void saveTodo() {
        saveButton.setEnabled(false);

        // basic validation
        boolean hasError = false;
        if (titleText.getText().toString().isEmpty()) {
            titleText.setError("Title is required");
            hasError = true;
        } else {
            titleText.setError(null);
        }
        if (!dateSet) {
            binding.dateButton.setError("Date is required");
            hasError = true;
        } else {
            binding.dateButton.setError(null);
        }
        if (!timeSet) {
            binding.timeButton.setError("Time is required");
            hasError = true;
        } else {
            binding.timeButton.setError(null);
        }

        if (hasError) {
            saveButton.setEnabled(true);
            return;
        }

        Calendar dueDate = Calendar.getInstance();
        // The values are set in the date and time picker dialogs
        dueDate.set(mYear, mMonth, mDay, mHour, mMinute);

        Priority priority = Priority.valueOf(binding.prioritySpinner.getSelectedItem().toString());
        Todo todo = new Todo(
                -1,
                titleText.getText().toString(),
                dueDate,
                descriptionText.getText().toString(),
                priority,
                isDoneSwitch.isChecked()
        ).save();

        Toast.makeText(this, "Saved: " + todo.getTitle() + ", Due at: " + todo.getDateTime() + ", with priority: " + todo.getPriority(), Toast.LENGTH_LONG).show();

        // returning to main activity refreshes the list automatically (onResume)
        finish();
    }
}
