package com.sergeygovorunov.imagecollection.adapters;

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

public class SelectCollectionViewAdapter extends RecyclerView.Adapter<SelectCollectionViewAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private File current = null;
    private ViewHolder previous = null;
    private final ArrayList<File> collections = new ArrayList<>();

    public SelectCollectionViewAdapter(Context context, File initDirectory, File currentCollection) {
        layoutInflater = LayoutInflater.from(context);
        collections.add(initDirectory);
        File[] newFiles = initDirectory.listFiles(File::isDirectory);
        if (newFiles != null) {
            Collections.addAll(collections, newFiles);
        }
        collections.remove(currentCollection);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.select_collection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            if (previous != null) {
                previous.itemView.setBackgroundColor(0xFFFFFFFF);
            }
            current = collections.get(getAdapterPosition());
            previous = this;
            itemView.setBackgroundColor(0x4C3700B3);
        }
    }

    public File getCurrent() {
        return current;
    }
}