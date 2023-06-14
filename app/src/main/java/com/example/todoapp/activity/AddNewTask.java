package com.example.todoapp.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.databinding.AddNewtaskBinding;
import com.example.todoapp.model.ToDoModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewTask extends AppCompatActivity {
    AddNewtaskBinding binding;
    private Boolean update;
    private DataBaseHelper myDb;
    private String timeStamp;
    ToDoModel toDoModel;
    Calendar calendar;

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
            binding.etTime.setText(toDoModel.getTime());
            binding.txtAMPM.setText(toDoModel.getAmpm());
            timeStamp = toDoModel.getTimestamp();
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtCancel.setOnClickListener(v -> onBackPressed());
        binding.etTime.setOnClickListener(v12 -> openDatePicker(binding.etTime));
        binding.txtAMPM.setOnClickListener(v12 -> openDatePicker(binding.etTime));

        binding.txtAdd.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.edittext.getText().toString())) {
                Toast.makeText(this, "Please enter task", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etTime.getText().toString())) {
                Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            } else {
                if (update) {
                    myDb.updateTask(toDoModel.getId(), binding.edittext.getText().toString(), binding.etMessage.getText().toString(), binding.etTime.getText().toString(), binding.txtAMPM.getText().toString(), timeStamp);
                } else {
                    ToDoModel item = new ToDoModel();
                    item.setTask(binding.edittext.getText().toString());
                    item.setMessage(binding.etMessage.getText().toString());
                    item.setStatus(0);
                    item.setTime(binding.etTime.getText().toString());
                    item.setAmpm(binding.txtAMPM.getText().toString());
                    item.setTimestamp(timeStamp);
                    myDb.insertTask(item);
                }

                Intent intent = new Intent(AddNewTask.this, MainActivity.class);
                setResult(900, intent);
                startActivity(intent);
                finish();
            }
        });
    }

    private void openDatePicker(final EditText button) {
        final Calendar calendar = Calendar.getInstance();
        if (update) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date date = null;
            try {
                date = sdf.parse(toDoModel.getTime() + " " + toDoModel.getAmpm());
            } catch (ParseException e) {
            }
            calendar.setTime(date);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            if (selectedHour < 12) {
                binding.txtAMPM.setText(getString(R.string.am));
                calendar.set(Calendar.AM_PM,0);
            } else {
                binding.txtAMPM.setText(getString(R.string.pm));
                calendar.set(Calendar.AM_PM,1);
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