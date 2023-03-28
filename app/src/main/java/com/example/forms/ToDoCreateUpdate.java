package com.example.forms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.forms.enums.Priority;
import com.example.forms.models.Todo;

import java.util.Calendar;
import java.util.Objects;

//public class ToDoCreateUpdate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
public class ToDoCreateUpdate extends AppCompatActivity {

    // Form variables
    private Button saveButton;
    private EditText titleText, descriptionText;
    private Spinner prioritySpinner;
    private SwitchCompat isDoneSwitch;

    // Calendar form variables
    Button datePicker, timePicker;
    boolean dateSet, timeSet = false;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_to_do_create_update);

        setTitle("Create new todo");

        titleText = findViewById(R.id.editTextTitle);
        isDoneSwitch = findViewById(R.id.isDoneSwitch);
        descriptionText = findViewById(R.id.editTextDescription);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        // Adapted from https://stackoverflow.com/questions/13377361/how-to-use-enum-values-in-a-spinner
        // values from enum are used to populate the spinner/ dropdown
        ArrayAdapter<Priority> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Priority.values());
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setSelection(0); // High priority by default

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveTodo());

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Adapted from https://www.digitalocean.com/community/tutorials/android-date-time-picker-dialog
        // calendar date and time form items.
        datePicker = findViewById(R.id.dateButton);
        timePicker = findViewById(R.id.timeButton);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    this::onDateSetListener, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
        timePicker.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    this::onTimeSetListener, mHour, mMinute, true);
            timePickerDialog.show();
        });
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
            datePicker.setError("Date is required");
            hasError = true;
        } else {
            datePicker.setError(null);
        }
        if (!timeSet) {
            timePicker.setError("Time is required");
            hasError = true;
        } else {
            timePicker.setError(null);
        }

        if (hasError) {
            saveButton.setEnabled(true);
            return;
        }

        Calendar dueDate = Calendar.getInstance();
        // The values are set in the date and time picker dialogs
        dueDate.set(mYear, mMonth, mDay, mHour, mMinute);

        Priority priority = Priority.valueOf(prioritySpinner.getSelectedItem().toString());
        Todo todo = new Todo(
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
