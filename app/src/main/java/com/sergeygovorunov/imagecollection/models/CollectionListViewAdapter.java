package com.sergeygovorunov.imagecollection.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View view = mInflater.inflate(R.layout.directory_chooser_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionListViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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