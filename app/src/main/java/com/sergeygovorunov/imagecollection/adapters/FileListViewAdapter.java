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

public class FileListViewAdapter extends RecyclerView.Adapter<FileListViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private OnItemChangedListener mClickListener;
    private ArrayList<File> files = new ArrayList<>();
    private int currentIndex = 0;

    public FileListViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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
            int adapterPosition = getAdapterPosition();
            if (mClickListener != null && currentIndex != adapterPosition) {
                ActionThatChangedItem itemChangedState = currentIndex > adapterPosition ?
                        ActionThatChangedItem.PREVIOUS : ActionThatChangedItem.NEXT;
                currentIndex = adapterPosition;
                mClickListener.onItemChanged(files.get(currentIndex), itemChangedState);
            }
        }
    }

    public File getCurrentItem() {
        return files.get(currentIndex);
    }

    public void removeCurrentItem(boolean isDeleted) {
        files.remove(currentIndex);
        notifyItemRemoved(currentIndex);
        if (files.size() > 0) {
            ActionThatChangedItem actionThatChangedItem = isDeleted ? ActionThatChangedItem.REMOVE_GET_NEXT : ActionThatChangedItem.MOVE_GET_NEXT;
            if (currentIndex >= files.size()) {
                currentIndex = files.size() - 1;
                actionThatChangedItem = isDeleted ? ActionThatChangedItem.REMOVE_GET_PREVIOUS : ActionThatChangedItem.MOVE_GET_PREVIOUS;
            }
            mClickListener.onItemChanged(files.get(currentIndex), actionThatChangedItem);
        } else {
            currentIndex = 0;
        }
    }

    public void next() {
        if (currentIndex < files.size() - 1 && mClickListener != null) {
            currentIndex++;
            mClickListener.onItemChanged(files.get(currentIndex), ActionThatChangedItem.NEXT);
        }
    }

    public void previous() {
        if (currentIndex > 0 && mClickListener != null) {
            currentIndex--;
            mClickListener.onItemChanged(files.get(currentIndex), ActionThatChangedItem.PREVIOUS);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCollection(File collection) {
        if (files.size() > 0) {
            files = new ArrayList<>();
        }
        File[] tmpFiles = collection.listFiles(File::isFile);
        for (File file : tmpFiles) {
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (extension.equals("bmp") || extension.equals("jpg") ||
                    extension.equals("jpeg") || extension.equals("png")) {
                files.add(file);
            }
        }
        notifyDataSetChanged();
        if (mClickListener != null && files.size() > 0) {
            mClickListener.onItemChanged(files.get(0), ActionThatChangedItem.NEXT);
        }
    }

    public void setOnItemClickListener(OnItemChangedListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface OnItemChangedListener {
        void onItemChanged(File item, ActionThatChangedItem changedState);
    }

    public enum ActionThatChangedItem {
        NEXT,
        PREVIOUS,
        REMOVE_GET_NEXT,
        REMOVE_GET_PREVIOUS,
        MOVE_GET_NEXT,
        MOVE_GET_PREVIOUS
    }
}