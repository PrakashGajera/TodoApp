package com.example.todoapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.database.DataBaseHelper;
import com.example.todoapp.R;
import com.example.todoapp.adapter.ToDoAdapter;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoAdapter.onClick {
    ActivityMainBinding binding;
    private DataBaseHelper myDB;
    private List<ToDoModel> mList;
    private ToDoAdapter adapter;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        FloatingActionButton fab = findViewById(R.id.fab);
        myDB = new DataBaseHelper(MainActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB, MainActivity.this, MainActivity.this);

        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        mList = myDB.getAllTasks();
        calendar = Calendar.getInstance();
        adapter.setTasks(mList, calendar.getTimeInMillis());

        if (mList.size() > 0) binding.recyclerview.setVisibility(View.VISIBLE);
        else binding.recyclerview.setVisibility(View.GONE);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddNewTask.class));
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 900) {
            mList = myDB.getAllTasks();
            calendar = Calendar.getInstance();
            adapter.setTasks(mList, calendar.getTimeInMillis());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(ToDoModel toDoModel, final Integer position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_delete);

        TextView txtDesc = dialog.findViewById(R.id.txtDesc);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtOk = dialog.findViewById(R.id.txtOk);

        txtDesc.setText(getString(R.string.delete_message, toDoModel.getTask()));

        txtOk.setOnClickListener(v12 -> {
            dialog.dismiss();
            myDB.deleteTask(toDoModel.getId());
            mList = myDB.getAllTasks();
            calendar = Calendar.getInstance();
            adapter.setTasks(mList, calendar.getTimeInMillis());
            adapter.notifyDataSetChanged();

            if (mList.size() > 0) {
                binding.recyclerview.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerview.setVisibility(View.GONE);
            }
        });

        txtCancel.setOnClickListener(v12 -> dialog.dismiss());
        dialog.show();
    }
}