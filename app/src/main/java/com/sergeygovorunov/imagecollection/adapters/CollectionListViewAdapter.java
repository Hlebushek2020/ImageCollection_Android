package com.sergeygovorunov.imagecollection.adapters;

import android.annotation.SuppressLint;
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
    private OnCollectionChangedListener mClickListener;
    private ArrayList<File> collections = new ArrayList<>();
    private int currentIndex = 0;

    public CollectionListViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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
                currentIndex = getAdapterPosition();
                mClickListener.onCollectionChanged(collections.get(currentIndex), currentIndex);
            }
        }
    }

    public File getCurrentCollection() {
        return collections.get(currentIndex);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBaseDirectory(File baseDirectory) {
        if (collections.size() > 0) {
            collections = new ArrayList<>();
        }
        File[] tmpDirectories = baseDirectory.listFiles(File::isDirectory);
        collections.add(baseDirectory);
        if (tmpDirectories != null) {
            Collections.addAll(collections, tmpDirectories);
        }
        notifyDataSetChanged();
        if (mClickListener != null && collections.size() > 0) {
            mClickListener.onCollectionChanged(baseDirectory, 0);
        }
    }

    public void setOnCollectionChangedListener(OnCollectionChangedListener onCollectionChangedListener) {
        this.mClickListener = onCollectionChangedListener;
    }

    public interface OnCollectionChangedListener {
        void onCollectionChanged(File collection, int position);
    }
}