package com.sergeygovorunov.imagecollection.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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
                mClickListener.onCollectionChanged(collections.get(currentIndex), null);
            }
        }
    }

    public File getBaseDirectory() {
        return collections.get(0);
    }

    public File getCurrentCollection() {
        return collections.get(currentIndex);
    }

    public void setBaseDirectory(File baseDirectory) {
        init(baseDirectory, null, null);
    }

    public void add(File collection) {
        collections.add(collection);
        notifyItemInserted(collections.size() - 1);
    }

    public void deleteCurrent() {
        // TODO: async task
        deleteRecursive(collections.get(currentIndex));
        notifyItemRemoved(currentIndex);
        if (collections.size() > 0) {
            if (currentIndex >= collections.size()) {
                currentIndex = collections.size() - 1;
            }
            mClickListener.onCollectionChanged(collections.get(currentIndex), null);
        } else {
            currentIndex = 0;
        }
    }

    public boolean renameCurrent(String newName) {
        File current = collections.get(currentIndex);
        File newCurrent = new File(current.getParent() + File.separatorChar + newName);
        if (current.renameTo(newCurrent)) {
            notifyItemChanged(currentIndex);
            return true;
        }
        return false;
    }

    public void setOnCollectionChangedListener(OnCollectionChangedListener onCollectionChangedListener) {
        this.mClickListener = onCollectionChangedListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void init(File baseDirectory, File selectedCollection, File selectedItem) {
        if (collections.size() > 0) {
            collections = new ArrayList<>();
        }
        File[] tmpDirectories = baseDirectory.listFiles(File::isDirectory);
        collections.add(baseDirectory);
        if (tmpDirectories != null) {
            Collections.addAll(collections, tmpDirectories);
        }
        if (selectedCollection != null) {
            currentIndex = collections.indexOf(selectedCollection);
        }
        notifyDataSetChanged();
        if (mClickListener != null && collections.size() > 0) {
            mClickListener.onCollectionChanged(collections.get(currentIndex), selectedItem);
        }
    }

    public interface OnCollectionChangedListener {
        void onCollectionChanged(File collection, File selectedItem);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}