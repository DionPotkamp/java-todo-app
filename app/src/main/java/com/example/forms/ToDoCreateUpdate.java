package com.example.forms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.forms.models.Todo;
import com.example.forms.utils.DateHelper;

import java.util.Calendar;

public class ToDoCreateUpdate extends AppCompatActivity {

    // Form variables
    private Button saveButton;
    private EditText titleText, descriptionText;
    private Spinner prioritySpinner;
    private SwitchCompat isDoneSwitch;

    // Calendar form variables
    Button datePicker, timePicker;
    EditText date, time;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_create_update);

        titleText = findViewById(R.id.editTextTitle);
        isDoneSwitch = findViewById(R.id.isDoneSwitch);
        descriptionText = findViewById(R.id.editTextDescription);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        // Adapted from https://stackoverflow.com/questions/13377361/how-to-use-enum-values-in-a-spinner
        // values from enum are used to populate the spinner/ dropdown
        ArrayAdapter<Todo.Priority> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Todo.Priority.values());
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setSelection(0); // High priority by default

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            saveButton.setEnabled(false);

            // basic validation
            boolean hasError = false;
            if (titleText.getText().toString().isEmpty()) {
                titleText.setError("Title is required");
                saveButton.setEnabled(true);
                hasError = true;
            }
            if (date.getText().toString().isEmpty()) {
                date.setError("Date is required");
                saveButton.setEnabled(true);
                hasError = true;
            } else if (!DateHelper.isValid(date.getText().toString())) {
                date.setError("Date is invalid");
                saveButton.setEnabled(true);
                hasError = true;
            } else {
                date.setError(null);
            }
            if (time.getText().toString().isEmpty()) {
                time.setError("Time is required");
                saveButton.setEnabled(true);
                hasError = true;
            } else if (!DateHelper.isValid(time.getText().toString())) {
                time.setError("Time is invalid");
                saveButton.setEnabled(true);
                hasError = true;
            } else {
                time.setError(null);
            }

            if (hasError) {
                return;
            }

            Calendar dueDate = Calendar.getInstance();
            dueDate.set(mYear, mMonth, mDay, mHour, mMinute);

            Todo.Priority priority = Todo.Priority.valueOf(prioritySpinner.getSelectedItem().toString());
            Todo todo = new Todo(
                    titleText.getText().toString(),
                    descriptionText.getText().toString(),
                    dueDate,
                    priority,
                    isDoneSwitch.isChecked()
            );

            MainActivity.dbHandler.addNewTodo(todo);
            Toast.makeText(this, "Saved: " + todo.getTitle() + ", Due at: " + todo.getDateTime() + ", with priority: " + todo.getPriority(), Toast.LENGTH_LONG).show();

            // returning to main activity refreshes the list automatically (onResume)
            finish();
        });

        findViewById(R.id.backButton)
                .setOnClickListener(v -> finish());

        // Adapted from https://www.digitalocean.com/community/tutorials/android-date-time-picker-dialog
        // calendar date and time form items.
        datePicker = findViewById(R.id.dateButton);
        timePicker = findViewById(R.id.timeButton);
        date = findViewById(R.id.dateText);
        time = findViewById(R.id.timeText);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        monthOfYear++;
                        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                        String yearString = String.valueOf(year).length() == 2 ? "20" + year : "" + year;

                        date.setText(day + "/" + month + "/" + yearString);
                        mYear = year;
                        mMonth = --monthOfYear;
                        mDay = dayOfMonth;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
        timePicker.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        time.setText(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
                        mHour = hourOfDay;
                        mMinute = minute;
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        });
    }
}
