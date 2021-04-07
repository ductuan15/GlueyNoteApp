package com.example.mynoteapp.data;

import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynoteapp.R;

import java.util.ArrayList;

public class TagAdapter  extends RecyclerView.Adapter<TagAdapter.TagHolder> {
    ArrayList<TagDetail> tagsArrayList;

    static class TagHolder extends RecyclerView.ViewHolder{
        protected TextView tagTextView;
        protected TextView tagNumView;
        public TagHolder(@NonNull View itemView) {
            super(itemView);
            this.tagTextView = itemView.findViewById(R.id.textView);
            this.tagNumView = itemView.findViewById(R.id.tagNum);
        }
    }

    public TagAdapter( ArrayList<TagDetail> tagsArrayList){
        this.tagsArrayList = tagsArrayList;
    }
    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagholder, parent, false);
        return new TagHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        holder.tagTextView.setText(this.tagsArrayList.get(position).getTitle());
        holder.tagNumView.setText(String.valueOf(this.tagsArrayList.get(position).getNum()));
    }

    @Override
    public int getItemCount() {
        return this.tagsArrayList.size();
    }
}
