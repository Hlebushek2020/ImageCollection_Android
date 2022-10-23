package com.sergeygovorunov.imagecollection;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sergeygovorunov.imagecollection.adapters.SelectCollectionViewAdapter;

import java.io.File;

public class SelectCollectionActivity extends AppCompatActivity {

    public static final String KEY_SELECTED_COLLECTION = "selectedColl";
    public static final String KEY_BASE_DIRECTORY = "selectedColl";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_chooser);
        Bundle bundle = getIntent().getExtras();
        File selectedCollection = (File) bundle.get(KEY_SELECTED_COLLECTION);
        File baseDirectory = (File) bundle.get(KEY_BASE_DIRECTORY);
        SelectCollectionViewAdapter selectCollectionViewAdapter =
                new SelectCollectionViewAdapter(this, baseDirectory, selectedCollection);
        RecyclerView recyclerView_directories = findViewById(R.id.folder_list);
        recyclerView_directories.setAdapter(selectCollectionViewAdapter);
    }
}