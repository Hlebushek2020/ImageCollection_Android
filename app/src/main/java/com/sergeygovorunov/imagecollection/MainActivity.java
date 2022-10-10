package com.sergeygovorunov.imagecollection;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sergeygovorunov.imagecollection.models.CollectionListViewAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> directoryChooser;

    private RecyclerView rv_collection_list;
    private RecyclerView rv_file_list;

    private CollectionListViewAdapter adapter_collection_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_collection_list = findViewById(R.id.collection_list);
        rv_file_list = findViewById(R.id.file_list);
        directoryChooser = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            File dir = (File) data.getExtras().get(DirectoryChooserActivity.KEY_SELECTED_FILE);
                            throw new RuntimeException(dir.getAbsolutePath());
                        }
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