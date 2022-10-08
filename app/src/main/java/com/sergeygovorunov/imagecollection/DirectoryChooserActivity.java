package com.sergeygovorunov.imagecollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
            @Override
            public void onItemClick(View view, File file, int position) {
                allDirectory.clear();
                File[] tmpFiles = file.listFiles(File::isDirectory);
                Collections.addAll(allDirectory, tmpFiles);
            }
        });
        rv_folder_list.setAdapter(folderLa);
        b_cancel = findViewById(R.id.cancel);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        b_ok = findViewById(R.id.ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}