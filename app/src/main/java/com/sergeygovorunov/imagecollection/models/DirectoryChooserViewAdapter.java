package com.sergeygovorunov.imagecollection.models;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DirectoryChooserViewAdapter extends RecyclerView.Adapter<DirectoryChooserViewAdapter.ViewHolder> {

    private File current;
    private ArrayList<File> directoriesInCurrent = new ArrayList<>();
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public DirectoryChooserViewAdapter(Context context, File initDirectory) {
        this.mInflater = LayoutInflater.from(context);
        current = initDirectory;
        File[] newFiles = initDirectory.listFiles(File::isDirectory);
        if (newFiles != null) {
            Collections.addAll(directoriesInCurrent, newFiles);
        }
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.directory_chooser_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = directoriesInCurrent.get(position);
        holder.directoryName.setText(file.getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return directoriesInCurrent.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView directoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            directoryName = itemView.findViewById(R.id.directoryName);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onClick(View view) {
            current = directoriesInCurrent.get(getAdapterPosition());
            directoriesInCurrent.clear();
            File[] newFiles = current.listFiles(File::isDirectory);
            if (newFiles != null) {
                Collections.addAll(directoriesInCurrent, newFiles);
            }
            notifyDataSetChanged();
        }
    }

    public File getCurrent() {
        return current;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void previous() {
        File parent = current.getParentFile();
        File[] newFiles = parent.listFiles(File::isDirectory);
        if (newFiles != null) {
            current = parent;
            directoriesInCurrent.clear();
            Collections.addAll(directoriesInCurrent, newFiles);
            notifyDataSetChanged();
        }
    }
}