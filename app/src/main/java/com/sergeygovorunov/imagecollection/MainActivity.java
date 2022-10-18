package com.sergeygovorunov.imagecollection;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sergeygovorunov.imagecollection.adapters.CollectionListViewAdapter;
import com.sergeygovorunov.imagecollection.adapters.FileListViewAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> directoryChooser;
    private GestureDetectorCompat gestureDetector;

    private RecyclerView rv_collection_list;
    private RecyclerView rv_file_list;
    private DrawerLayout main_drawer_layout;
    private ImageSwitcher image_switcher;

    private CollectionListViewAdapter collectionListViewAdapter;
    private FileListViewAdapter fileListViewAdapter;

    private int drawerState;
    //private Object drawerStateSync = new Object();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        fileListViewAdapter = new FileListViewAdapter(this);
        fileListViewAdapter.setOnItemClickListener((item, position) -> {
            Bitmap bitmapOrig = BitmapFactory.decodeFile(item.getPath());
            double w = bitmapOrig.getWidth();
            double h = bitmapOrig.getHeight();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            double nw = displayMetrics.widthPixels;
            double nh = displayMetrics.heightPixels;
            if (w > h) {
                nh = nw / (w / h);
            } else {
                nw = nh / (h / w);
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(bitmapOrig, (int) nw, (int) nh, false);
            Drawable drawable = new BitmapDrawable(bitmap);
            image_switcher.setImageDrawable(drawable);
            main_drawer_layout.closeDrawers();
        });
        rv_file_list = findViewById(R.id.file_list);
        rv_file_list.setAdapter(fileListViewAdapter);
        //
        collectionListViewAdapter = new CollectionListViewAdapter(this);
        main_drawer_layout = findViewById(R.id.main_drawer_layout);
        collectionListViewAdapter.setOnCollectionChangedListener((collection, position) -> {
            fileListViewAdapter.setCollection(collection);
            main_drawer_layout.closeDrawers();
        });
        rv_collection_list = findViewById(R.id.collection_list);
        rv_collection_list.setAdapter(collectionListViewAdapter);
        //
        image_switcher = findViewById(R.id.image_switcher);
        image_switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.image_switcher_in));
        image_switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.image_switcher_out));
        image_switcher.setFactory(() -> {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(0xFFFFFFFF);
            return imageView;
        });
        //
        directoryChooser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = result.getData().getExtras();
                File baseDirectory = (File) bundle.get(DirectoryChooserActivity.KEY_SELECTED_FILE);
                collectionListViewAdapter.setBaseDirectory(baseDirectory);
            }
        });
        //
        gestureDetector = new GestureDetectorCompat(this, new SimpleGestureListener());
        main_drawer_layout.setOnTouchListener((view, motionEvent) -> {
            //synchronized (drawerStateSync) {
            if (drawerState == DrawerLayout.STATE_IDLE) {
                gestureDetector.onTouchEvent(motionEvent);
            }
            //}
            return false;
        });
        main_drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //synchronized (drawerStateSync) {
                drawerState = newState;
                //}
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

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            float size = Math.abs(motionEvent.getX() - motionEvent1.getX());
            if (size > 50.0d) {
                if (motionEvent.getX() > motionEvent1.getX()) {
                    fileListViewAdapter.next();
                } else if (motionEvent.getX() < motionEvent1.getX()) {
                    fileListViewAdapter.previous();
                }
            }
            return true;
        }
    }
}