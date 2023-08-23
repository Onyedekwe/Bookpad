package com.ebookfrenzy.lecturenote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.ebookfrenzy.lecturenote.R;
import com.ebookfrenzy.lecturenote.TodoActivity;

import java.util.ArrayList;

public class Todo_adapter extends RecyclerView.Adapter<Todo_adapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> todo_idFilter;
    private final OnNoteListener onNoteListener;
    DatabaseHelper db;
    private ArrayList<String> todo_id;


    public Todo_adapter(Context context, ArrayList<String> todo_id, OnNoteListener onNoteListener) {
        this.context = context;
        this.todo_id = todo_id;
        this.todo_idFilter = todo_id;
        this.onNoteListener = onNoteListener;
        db = new DatabaseHelper(context.getApplicationContext());
    }

    public void setFilterList(ArrayList<String> filterList) {
        this.todo_id = filterList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new MyViewHolder(v, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(String.valueOf(todo_id.get(position)));
        Cursor res = db.getstatus(holder.textView.getText().toString());
        if (res.getCount() == 0) {

        }
        while (res.moveToNext()) {
            holder.checkBox.setChecked(getStat(res.getInt(2)));
        }

    }

    @Override
    public int getItemCount() {
        return todo_id.size();
    }

    public Context getContext() {
        return context;
    }

    public String getTaskName(int position) {
        return todo_id.get(position);
    }

    public boolean getStat(int stat) {
        return stat == 1;
    }


    public void refreshRemoved(int position) {
        todo_id.remove(position);
    }

    public void checkEmpty() {
        if (todo_id.isEmpty()) {
            Intent intent = new Intent(context, TodoActivity.class);
            context.startActivity(intent);
        } else {

        }
    }

    public void refreshUpdate(String task, int position) {
        todo_id.set(position, task);
    }

    public interface OnNoteListener {
        void onNoteClick(int position);

        void onItemLongClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CheckBox checkBox;
        TextView textView;
        OnNoteListener onNoteListener;


        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.textbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        db.updateStatus(textView.getText().toString(), 1);
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        db.updateStatus(textView.getText().toString(), 0);
                        textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                    }
                }
            });
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            onNoteListener.onItemLongClick(getAdapterPosition());
            return true;
        }
    }

}
