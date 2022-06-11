package com.example.diarycloud;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListDiaryAdapter extends RecyclerView.Adapter<ListDiaryAdapter.ViewHolder>{
    private ArrayList<Memory> diary;
    private OnItemListener onItemListener;

    public ListDiaryAdapter(ArrayList<Memory> diary, OnItemListener onItemListener) {
        this.diary = diary;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_memory, parent, false);
        return new ViewHolder(listItem, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Memory memory = diary.get(position);
        holder.memMedia.setImageURI(Uri.parse(memory.getMemMedia()));
        holder.memTitle.setText(memory.getMemTitle());
        holder.memFeeling.setText(memory.getMemFeeling());
        holder.memDate.setText(memory.getMemDate());
        holder.memLocation.setText(memory.getMemAddress());
    }

    @Override
    public int getItemCount() {
        return diary.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView memMedia;
        private TextView memTitle;
        private TextView memFeeling;
        private TextView memLocation;
        private TextView memDate;
        private OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            memMedia = (ImageView) itemView.findViewById(R.id.item_cover);
            memTitle = (TextView) itemView.findViewById(R.id.item_title);
            memFeeling = (TextView) itemView.findViewById(R.id.item_emoji);
            memLocation = (TextView) itemView.findViewById(R.id.item_location);
            memDate = (TextView) itemView.findViewById(R.id.item_date);

            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }

        public ImageView getMemMedia() {
            return memMedia;
        }

        public void setMemMedia(ImageView memMedia) {
            this.memMedia = memMedia;
        }

        public TextView getMemTitle() {
            return memTitle;
        }

        public void setMemTitle(TextView memTitle) {
            this.memTitle = memTitle;
        }

        public TextView getMemFeeling() {
            return memFeeling;
        }

        public void setMemFeeling(TextView memFeeling) {
            this.memFeeling = memFeeling;
        }

        public TextView getMemLocation() {
            return memLocation;
        }

        public void setMemLocation(TextView memLocation) {
            this.memLocation = memLocation;
        }

        public TextView getMemDate() {
            return memDate;
        }

        public void setMemDate(TextView memDate) {
            this.memDate = memDate;
        }
    }
    public interface OnItemListener{
        void onItemClick(int position);
    }
}
