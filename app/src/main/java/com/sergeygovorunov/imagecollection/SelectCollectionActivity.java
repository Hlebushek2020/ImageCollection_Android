package com.sergeygovorunov.imagecollection;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
        File currentCollection = (File) bundle.get(KEY_SELECTED_COLLECTION);
        File baseDirectory = (File) bundle.get(KEY_BASE_DIRECTORY);
        SelectCollectionViewAdapter selectCollectionViewAdapter =
                new SelectCollectionViewAdapter(this, baseDirectory, currentCollection);
        RecyclerView recyclerView_directories = findViewById(R.id.folder_list);
        recyclerView_directories.setAdapter(selectCollectionViewAdapter);
        Button button_ok = findViewById(R.id.ok);
        button_ok.setOnClickListener(view -> {
            File selectedCollection = selectCollectionViewAdapter.getCurrent();
            if (selectedCollection != null) {
                Intent data = new Intent();
                data.putExtra(KEY_SELECTED_COLLECTION, selectedCollection);
                setResult(RESULT_OK, data);
                finish();
            } else {
                AlertDialog.Builder notSelectedAlert = new AlertDialog.Builder(this)
                        .setMessage("Пожалуйста выберите коллекцию!")
                        .setNeutralButton("Ок", (dialogInterface, id) -> {
                            dialogInterface.dismiss();
                        });
                notSelectedAlert.show();
            }
        });
    }
}