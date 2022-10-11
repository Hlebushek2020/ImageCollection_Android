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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;

public class FileListViewAdapter extends RecyclerView.Adapter<FileListViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private ArrayList<File> files = new ArrayList<>();

    public FileListViewAdapter(Context context, File current) {
        mInflater = LayoutInflater.from(context);
        File[] tmpFiles = current.listFiles(File::isFile);
        for (File file : tmpFiles) {
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (extension.equals("bmp") || extension.equals("jpg") ||
                    extension.equals("jpeg") || extension.equals("png")) {
                files.add(file);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = files.get(position);
        holder.directoryName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
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
        return files.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, File file, int position);
    }
}