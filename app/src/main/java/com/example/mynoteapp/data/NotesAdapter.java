package com.example.mynoteapp.data;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.mynoteapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements Filterable {
    ArrayList<NoteDetail> noteDetailsArrayList;
    ArrayList<NoteDetail> filteresNoteDetailArrayList;
    private final AdapterView.OnItemClickListener onItemClickListener;
    Context context;

    public NotesAdapter(ArrayList<NoteDetail> noteDetailsArrayList, AdapterView.OnItemClickListener onItemClickListener, Context context) {
        this.noteDetailsArrayList = noteDetailsArrayList;
        this.filteresNoteDetailArrayList = new ArrayList<NoteDetail>();
        this.filteresNoteDetailArrayList.addAll(noteDetailsArrayList);
        this.onItemClickListener = onItemClickListener;
        this.context = context;

    }


    public void setArrayList(ArrayList<NoteDetail> arrayList) {
        noteDetailsArrayList = arrayList;
        filteresNoteDetailArrayList.clear();
        filteresNoteDetailArrayList.addAll(noteDetailsArrayList);
        //TODO: review this line
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView briefContent_textView;
        TextView modifiedDate_textView;
        TextView title_textview;
        ChipGroup tagChipGroup;
        private final AdapterView.OnItemClickListener onItemClickListener;


        public NoteViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener onItemClickListener) {
            super(itemView);
            this.title_textview = itemView.findViewById(R.id.title_textview);
            this.briefContent_textView = itemView.findViewById(R.id.briefContent_textview);
            this.modifiedDate_textView = itemView.findViewById(R.id.modifiedDate_textview);
            this.tagChipGroup = itemView.findViewById(R.id.tags_chipgroup);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }


        public TextView getBriefContent_textView() {
            return this.briefContent_textView;
        }

        public TextView getModifiedDate_textView() {
            return this.modifiedDate_textView;
        }

        public TextView getTitle_textview() {
            return this.title_textview;
        }

        @Override
        public void onClick(View v) {
            this.onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());
        }
    }

    public NoteDetail getNoteDetailAt(int position) {
        return this.filteresNoteDetailArrayList.get(position);
    }

    @NonNull
    @Override
    public NotesAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteholder, parent, false);
        return new NoteViewHolder(view, this.onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        NoteDetail noteDetail = this.filteresNoteDetailArrayList.get(position);

        //holder.getBriefContent_textView().loadDataWithBaseURL(null, noteDetail.getContent(), "text/html", "UTF-8", "");
        Spanned htmlContent = Html.fromHtml(noteDetail.getContent());
        holder.briefContent_textView.setText(htmlContent, TextView.BufferType.SPANNABLE);
        holder.modifiedDate_textView.setText(NoteDetail.simpleDateFormat.format(noteDetail.getEditDate()));
        holder.title_textview.setText(noteDetail.getTitle());
        holder.tagChipGroup.removeAllViews();
        for(String tag: noteDetail.getAllTags()){
            Chip newChip = new Chip(this.context);
            newChip.setText(tag);
            holder.tagChipGroup.addView(newChip);
        }
    }


    @Override
    public int getItemCount() {
        return filteresNoteDetailArrayList.size();
    }

    public long getNoteID(int position) {
        return this.filteresNoteDetailArrayList.get(position).getID();
    }
    public ArrayList<NoteDetail> getNoteDetailsArrayList(){
        return this.noteDetailsArrayList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String constraintString = constraint.toString();
                FilterResults filterResults = new FilterResults();
                ArrayList<NoteDetail> filteredList = new ArrayList<NoteDetail>();
                if (constraintString.length() == 0) {
                    filteredList.addAll(noteDetailsArrayList);
                } else {
                    for (NoteDetail noteDetail : noteDetailsArrayList) {
                        if (noteDetail.isHaveString(constraintString)) {
                            filteredList.add(noteDetail);
                        }
                    }
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteresNoteDetailArrayList.clear();
                filteresNoteDetailArrayList = (ArrayList<NoteDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
