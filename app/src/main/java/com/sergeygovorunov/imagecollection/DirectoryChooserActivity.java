package com.sergeygovorunov.imagecollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.sergeygovorunov.imagecollection.models.DirectoryChooserViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DirectoryChooserActivity extends AppCompatActivity {

    public static final String KEY_SELECTED_FILE = "selectedFile";

    private RecyclerView rv_folder_list;
    private Button b_cancel;
    private Button b_ok;

    private File currentFolder;
    private DirectoryChooserViewAdapter folderLa;
    private ArrayList<File> allDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_chooser);
        rv_folder_list = findViewById(R.id.folder_list);
        currentFolder = Environment.getExternalStorageDirectory();
        File[] tmpFiles = currentFolder.listFiles(File::isDirectory);
        allDirectory = new ArrayList<>(tmpFiles != null ?
                Arrays.asList(tmpFiles) : new ArrayList<>());
        folderLa = new DirectoryChooserViewAdapter(this, allDirectory);
        folderLa.setOnItemClickListener(new DirectoryChooserViewAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(View view, File file, int position) {
                allDirectory.clear();
                currentFolder = file;
                File[] tmpFiles = file.listFiles(File::isDirectory);
                if (tmpFiles != null) {
                    Collections.addAll(allDirectory, tmpFiles);
                    folderLa.notifyDataSetChanged();
                }
            }
        });
        rv_folder_list.setAdapter(folderLa);
        DirectoryChooserActivity dsa = this;
        b_ok = findViewById(R.id.ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra(KEY_SELECTED_FILE, currentFolder);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBackPressed() {
        if (Environment.getExternalStorageDirectory().equals(currentFolder)) {
            super.onBackPressed();
        } else {
            currentFolder = currentFolder.getParentFile();
            File[] tmpFiles = currentFolder.listFiles(File::isDirectory);
            if (tmpFiles != null) {
                allDirectory.clear();
                Collections.addAll(allDirectory, tmpFiles);
                folderLa.notifyDataSetChanged();
            }
        }
    }
}