package com.sergeygovorunov.imagecollection.adapters;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sergeygovorunov.imagecollection.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

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

    public void deleteCurrent(Context ctx) {
        int notificationId = ThreadLocalRandom.current().nextInt();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);

        String channelId = getCurrentCollection().getPath();
        NotificationChannel channel = new NotificationChannel(channelId,
                "Delete Collection", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelId)
                .setContentTitle(ctx.getString(R.string.delete_collection_notification_title,
                        getCurrentCollection().getName()))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setProgress(100, 0, true);
        notificationManager.notify(notificationId, builder.build());

        Handler handler = new Handler(Looper.getMainLooper());

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> {
            File collection = collections.remove(currentIndex);
            handler.post(() -> {
                notifyItemRemoved(currentIndex);
            });
            if (collections.size() > 0) {
                if (currentIndex >= collections.size()) {
                    currentIndex = collections.size() - 1;
                }
                handler.post(() -> {
                    mClickListener.onCollectionChanged(collections.get(currentIndex), null);
                });
                if (!deleteRecursive(ctx, collection, notificationManager, notificationId, builder)) {
                    collections.add(collection);
                    builder.setProgress(100, 100, false)
                            .setContentText(ctx.getString(R.string.delete_collection_notification_error));
                    notificationManager.notify(notificationId, builder.build());
                } else {
                    notificationManager.cancel(notificationId);
                }
            } else {
                currentIndex = 0;
            }
        });
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
        currentIndex = 0;
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

    private boolean deleteRecursive(Context ctx, File item, NotificationManagerCompat notificationManager,
                                    int notificationId, NotificationCompat.Builder builder) {
        boolean success = true;
        if (item.isDirectory()) {
            for (File child : item.listFiles()) {
                success = deleteRecursive(ctx, child, notificationManager, notificationId, builder);
            }
        }
        try {
            //Thread.sleep(10000);
            builder.setContentText(ctx.getString(R.string.delete_collection_notification_text,
                    item.getPath().substring(getBaseDirectory().getPath().length())));
            notificationManager.notify(notificationId, builder.build());
            success = success && item.delete();
        } catch (Exception ex) {
            success = false;
        }
        return success;
    }
}