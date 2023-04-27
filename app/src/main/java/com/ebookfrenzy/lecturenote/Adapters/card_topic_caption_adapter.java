package com.ebookfrenzy.lecturenote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.R;

import java.util.ArrayList;

public class card_topic_caption_adapter extends RecyclerView.Adapter<card_topic_caption_adapter.MyViewHolder> {
    private final Context context;
    private final OnNoteListener onNoteListener;
    String temp;
    int position;
    private ArrayList topic;

    public card_topic_caption_adapter(Context context, ArrayList topic, OnNoteListener onNoteListener) {
        this.context = context;
        this.topic = topic;
        this.onNoteListener = onNoteListener;
    }

    public void setFilterList(ArrayList<String> filterList) {
        this.topic = filterList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_topic_caption, parent, false);
        return new MyViewHolder(v, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull card_topic_caption_adapter.MyViewHolder holder, int position) {
        holder.topic.setText(String.valueOf(topic.get(position)));
        position = position;
    }


    @Override
    public int getItemCount() {
        return topic.size();
    }


    public interface OnNoteListener {
        void onNoteClick(int posiion);

        void onItemLongClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView topic;

        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic_name_text);
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