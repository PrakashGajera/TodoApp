package com.example.todoapp.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.databinding.TaskLayoutBinding;
import com.example.todoapp.model.ToDoModel;
import com.example.todoapp.activity.AddNewTask;
import com.example.todoapp.activity.MainActivity;
import com.example.todoapp.database.DataBaseHelper;

import java.util.Calendar;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    private List<ToDoModel> mList;
    MainActivity activity;
    DataBaseHelper myDB;
    onClick onClick;
    private long timeInMillis;

    public ToDoAdapter(DataBaseHelper myDB, MainActivity activity, onClick onClick) {
        this.activity = activity;
        this.myDB = myDB;
        this.onClick = onClick;
    }

    public interface onClick {
        void deleteItem(ToDoModel toDoModel, Integer position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskLayoutBinding binding = TaskLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ToDoModel item = mList.get(position);

        holder.binding.txtTitle.setText(item.getTask());
        holder.binding.txtMessage.setText(item.getMessage());
        holder.binding.txtTime.setText(item.getTime());
        holder.binding.txtAMPM.setText(item.getAmpm());

        if (item.getStatus() == 1) {
            holder.binding.txtTitle.setPaintFlags(holder.binding.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.txtMessage.setPaintFlags(holder.binding.txtMessage.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            if (timeInMillis > Long.parseLong(item.getTimestamp())) {
                holder.binding.txtTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
                holder.binding.txtMessage.setVisibility(View.VISIBLE);
                holder.binding.txtMessage.setText(activity.getResources().getString(R.string.pending));
            }
        }

        holder.binding.imgClose.setOnClickListener(v -> onClick.deleteItem(mList.get(position), position));
        holder.binding.llItem.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, AddNewTask.class).putExtra("update", true).putExtra("data", item));
            activity.finish();
        });

        holder.binding.mcheckbox.setChecked(toBoolean(item.getStatus()));
        holder.binding.mcheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                myDB.updateStatus(item.getId(), 1);
                holder.binding.txtTitle.setTextColor(ContextCompat.getColor(activity, R.color.black));
                holder.binding.txtMessage.setVisibility(View.GONE);
                holder.binding.txtTitle.setPaintFlags(holder.binding.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.binding.txtMessage.setPaintFlags(holder.binding.txtMessage.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                timeInMillis = Calendar.getInstance().getTimeInMillis();
                if (timeInMillis > Long.parseLong(item.getTimestamp())) {
                    holder.binding.txtTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
                    holder.binding.txtMessage.setVisibility(View.VISIBLE);
                    holder.binding.txtMessage.setText(activity.getResources().getString(R.string.pending));
                }
                myDB.updateStatus(item.getId(), 0);
                holder.binding.txtTitle.setPaintFlags(holder.binding.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.binding.txtMessage.setPaintFlags(holder.binding.txtMessage.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
    }

    public boolean toBoolean(int num) {
        return num != 0;
    }

    public void setTasks(List<ToDoModel> mList, long timeInMillis) {
        this.timeInMillis = timeInMillis;
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TaskLayoutBinding binding;

        public MyViewHolder(TaskLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
