package com.ebookfrenzy.lecturenote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.ebookfrenzy.lecturenote.R;

import java.util.ArrayList;

public class card_course_caption_adapter extends RecyclerView.Adapter<card_course_caption_adapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> course_idFilter;
    private final OnNoteListener onNoteListener;
    DatabaseHelper db;
    private ArrayList<String> course_id;


    public card_course_caption_adapter(Context context, ArrayList course_id, OnNoteListener onNoteListener) {
        this.context = context;
        this.course_id = course_id;
        this.course_idFilter = course_id;
        this.onNoteListener = onNoteListener;
        db = new DatabaseHelper(context.getApplicationContext());
    }

    public void setFilterList(ArrayList<String> filterList) {
        this.course_id = filterList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_course_caption, parent, false);
        return new MyViewHolder(v, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.course_id.setText(String.valueOf(course_id.get(position)));
    }

    @Override
    public int getItemCount() {
        return course_id.size();
    }


    public interface OnNoteListener {
        void onNoteClick(int position);

        void onItemLongClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView course_id;
        OnNoteListener onNoteListener;


        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            course_id = itemView.findViewById(R.id.course_name_text);
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
