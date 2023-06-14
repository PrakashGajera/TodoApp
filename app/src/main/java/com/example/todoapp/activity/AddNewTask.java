package com.example.todoapp.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.databinding.AddNewtaskBinding;
import com.example.todoapp.model.ToDoModel;

import java.util.Calendar;
import java.util.Locale;

public class AddNewTask extends AppCompatActivity {
    AddNewtaskBinding binding;
    private Boolean update;
    private DataBaseHelper myDb;
    private String selectedItem;
    private String timeStamp;
    ToDoModel toDoModel;
    Calendar calendar;
    int currentHour;
    int currentMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddNewtaskBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        myDb = new DataBaseHelper(this);
        calendar = Calendar.getInstance();

        update = getIntent().getBooleanExtra("update", false);
        if (update) {
            binding.txtAdd.setText(getString(R.string.update));
            toDoModel = (ToDoModel) getIntent().getSerializableExtra("data");
            binding.edittext.setText(toDoModel.getTask());
            binding.etMessage.setText(toDoModel.getMessage());
            binding.etIssueDate.setText(toDoModel.getTime());
            selectedItem = toDoModel.getAmpm();
            String[] time = toDoModel.getTime().split(":");
            currentHour = Integer.parseInt(time[0]);
            currentMinute = Integer.parseInt(time[1]);
            if (toDoModel.getAmpm().equalsIgnoreCase("am"))
                binding.spinnerTime.setSelection(0);
            else
                binding.spinnerTime.setSelection(1);
            timeStamp = toDoModel.getTimestamp();
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtCancel.setOnClickListener(v -> onBackPressed());
        binding.etIssueDate.setOnClickListener(v12 -> openDatePicker(binding.etIssueDate));

        binding.spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                if (update) {
                    if (position == 0) {
                        calendar.set(Calendar.HOUR, currentHour);
                        calendar.set(Calendar.MINUTE, currentMinute);
                        calendar.set(Calendar.AM_PM, Calendar.AM);
                    } else {
                        calendar.set(Calendar.HOUR, currentHour);
                        calendar.set(Calendar.MINUTE, currentMinute);
                        calendar.set(Calendar.AM_PM, Calendar.PM);
                    }
                    timeStamp = String.valueOf(calendar.getTimeInMillis());
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.txtAdd.setOnClickListener(v -> {
            if (update) {
                myDb.updateTask(toDoModel.getId(), binding.edittext.getText().toString(), binding.etMessage.getText().toString(), binding.etIssueDate.getText().toString(), selectedItem, timeStamp);
            } else {
                ToDoModel item = new ToDoModel();
                item.setTask(binding.edittext.getText().toString());
                item.setMessage(binding.etMessage.getText().toString());
                item.setStatus(0);
                item.setTime(binding.etIssueDate.getText().toString());
                item.setAmpm(selectedItem);
                item.setTimestamp(timeStamp);
                myDb.insertTask(item);
            }

            Intent intent = new Intent(AddNewTask.this, MainActivity.class);
            setResult(900, intent);
            startActivity(intent);
            finish();
        });
    }

    private void openDatePicker(final EditText button) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            currentHour = selectedHour;
            currentMinute = selectedMinute;
            calendar.set(Calendar.HOUR, currentHour);
            calendar.set(Calendar.MINUTE, currentMinute);
            calendar.set(Calendar.SECOND, 0);
            if (selectedHour < 12) {
                binding.spinnerTime.setSelection(0);
                calendar.set(Calendar.AM_PM, Calendar.AM);
            } else {
                binding.spinnerTime.setSelection(1);
                calendar.set(Calendar.AM_PM, Calendar.PM);
            }
            timeStamp = String.valueOf(calendar.getTimeInMillis());
            button.setText(String.format(Locale.getDefault(), "%02d:%02d", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute));
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddNewTask.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}