package com.sergeygovorunov.imagecollection;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.sergeygovorunov.imagecollection.adapters.CollectionListViewAdapter;
import com.sergeygovorunov.imagecollection.adapters.FileListViewAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> directoryChooser;

    private RecyclerView rv_collection_list;
    private RecyclerView rv_file_list;
    private DrawerLayout main_drawer_layout;

    private CollectionListViewAdapter collectionListViewAdapter;
    private FileListViewAdapter fileListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileListViewAdapter = new FileListViewAdapter(this);
        fileListViewAdapter.setOnItemClickListener((item, position) -> {

        });
        rv_file_list = findViewById(R.id.file_list);
        rv_file_list.setAdapter(fileListViewAdapter);
        collectionListViewAdapter = new CollectionListViewAdapter(this);
        main_drawer_layout = findViewById(R.id.main_drawer_layout);
        collectionListViewAdapter.setOnCollectionChangedListener((collection, position) -> {
            fileListViewAdapter.setCollection(collection);
            main_drawer_layout.closeDrawers();
        });
        rv_collection_list = findViewById(R.id.collection_list);
        rv_collection_list.setAdapter(collectionListViewAdapter);
        directoryChooser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = result.getData().getExtras();
                File baseDirectory = (File) bundle.get(DirectoryChooserActivity.KEY_SELECTED_FILE);
                collectionListViewAdapter.setBaseDirectory(baseDirectory);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_folder) {
            Intent chooseFile = new Intent(this, DirectoryChooserActivity.class);
            directoryChooser.launch(chooseFile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}