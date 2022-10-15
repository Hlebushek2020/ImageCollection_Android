package com.sergeygovorunov.imagecollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import com.sergeygovorunov.imagecollection.adapters.DirectoryChooserViewAdapter;

import java.io.File;

public class DirectoryChooserActivity extends AppCompatActivity {

    public static final String KEY_SELECTED_FILE = "selectedFile";

    private DirectoryChooserViewAdapter directoryChooserViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_chooser);
        RecyclerView recyclerView_directories = findViewById(R.id.folder_list);
        File current = Environment.getExternalStorageDirectory();
        directoryChooserViewAdapter = new DirectoryChooserViewAdapter(this, current);
        recyclerView_directories.setAdapter(directoryChooserViewAdapter);
        Button button_ok = findViewById(R.id.ok);
        button_ok.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(KEY_SELECTED_FILE, directoryChooserViewAdapter.getCurrent());
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        File current = directoryChooserViewAdapter.getCurrent();
        if (Environment.getExternalStorageDirectory().equals(current)) {
            super.onBackPressed();
        } else {
            directoryChooserViewAdapter.previous();
        }
    }
}