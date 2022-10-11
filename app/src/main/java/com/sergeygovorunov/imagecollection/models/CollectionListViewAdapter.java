package com.sergeygovorunov.imagecollection.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sergeygovorunov.imagecollection.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CollectionListViewAdapter extends RecyclerView.Adapter<CollectionListViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private ArrayList<File> collections = new ArrayList<>();

    public CollectionListViewAdapter(Context context, File current) {
        mInflater = LayoutInflater.from(context);
        collections.add(current);
        File[] tmpDirectories = current.listFiles(File::isDirectory);
        Collections.addAll(collections, tmpDirectories);
    }

    @NonNull
    @Override
    public CollectionListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.collection_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionListViewAdapter.ViewHolder holder, int position) {
        File file = collections.get(position);
        holder.directoryName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView directoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            directoryName = itemView.findViewById(R.id.directoryName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                int position = getAdapterPosition();
                mClickListener.onItemClick(view, getItem(position), position);
            }
        }
    }

    File getItem(int position) {
        return collections.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, File file, int position);
    }
}