package com.ebookfrenzy.lecturenote;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Adapters.Todo_adapter;
import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;

import java.util.ArrayList;

public class RecyclerViewTouchHelper extends ItemTouchHelper.SimpleCallback {


    private final Todo_adapter adapter;
    DatabaseHelper db;
    ArrayList<String> checktask;


    public RecyclerViewTouchHelper(Todo_adapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Task?");
            builder.setMessage("This action cannot be undone");
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    adapter.notifyItemChanged(position);
                }
            });
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String temp = adapter.getTaskName(position);
                    db = new DatabaseHelper(adapter.getContext());
                    db.deletetask(temp);
                    Toast.makeText(adapter.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                    adapter.refreshRemoved(position);
                    adapter.notifyItemRemoved(position);
                    adapter.checkEmpty();


                }
            });


            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(position);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            Button a = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            int buttonColor1 = ContextCompat.getColor(adapter.getContext(), R.color.black);
            if (a != null) {
                a.setTextColor(buttonColor1);
            }

            Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            int buttonColor2 = ContextCompat.getColor(adapter.getContext(), R.color.red_color);
            if (b != null) {
                b.setTextColor(buttonColor2);
                b.setPadding(50, 0, 10, 0);

            }
        } else {
            String temp = adapter.getTaskName(position);
            db = new DatabaseHelper(adapter.getContext());
            showEditDialog(temp, position);

        }


    }


    private void showEditDialog(String prevTask, int position) {

        final Dialog dialog = new Dialog(adapter.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomtaskeditlayout);
        TextView header = dialog.findViewById(R.id.Choosetxt);
        header.setText(R.string.EditTask);
        EditText taskText = dialog.findViewById(R.id.course_name);
        taskText.setHint("Insert task");
        taskText.setText(prevTask);
        Button saveButton = dialog.findViewById(R.id.layoutButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                String taskDemo = taskText.getText().toString();
                checktask = new ArrayList<>();

                Cursor res = db.gettask();
                while (res.moveToNext()) {

                    checktask.add(res.getString(1).trim());
                }

                if (!taskDemo.isEmpty()) {
                    if (checktask.contains(taskDemo.trim())) {
                        Toast.makeText(adapter.getContext(), "Task already exist", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean checkeditdata = db.updatetask(taskDemo, prevTask);
                        if (checkeditdata) {
                            Toast.makeText(adapter.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();


                            dialog.dismiss();

                            adapter.refreshUpdate(taskDemo, position);
                            adapter.notifyItemChanged(position);

                        } else {
                            Toast.makeText(adapter.getContext(), R.string.unsuccessful, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(adapter.getContext(), R.string.please_insert_task, Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                adapter.notifyItemChanged(position);
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}
