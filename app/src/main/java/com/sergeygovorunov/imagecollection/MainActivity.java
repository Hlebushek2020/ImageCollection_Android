package com.sergeygovorunov.imagecollection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sergeygovorunov.imagecollection.adapters.CollectionListViewAdapter;
import com.sergeygovorunov.imagecollection.adapters.FileListViewAdapter;
import com.sergeygovorunov.imagecollection.dialogs.InputAlertDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String SESSION_FILE_NAME = "session.txt";

    private ActivityResultLauncher<Intent> directoryChooser;
    private ActivityResultLauncher<Intent> selectToCollection;
    private Intent selectToCollectionIntent;
    private GestureDetectorCompat gestureDetector;

    private RecyclerView rv_collection_list;
    private RecyclerView rv_file_list;
    private DrawerLayout main_drawer_layout;
    private ImageSwitcher image_switcher;
    private Menu optMenu;

    private CollectionListViewAdapter collectionListViewAdapter;
    private FileListViewAdapter fileListViewAdapter;

    private Animation image_switcher_lin;
    private Animation image_switcher_lout;
    private Animation image_switcher_rin;
    private Animation image_switcher_rout;
    private Animation image_switcher_down_p1;
    //private Animation image_switcher_down_p2;
    private Animation image_switcher_up_p1;
    //private Animation image_switcher_up_p2;

    private int drawerState;
    //private Object drawerStateSync = new Object();

    private AlertDialog.Builder deleteFileAlert;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        image_switcher = findViewById(R.id.image_switcher);
        rv_file_list = findViewById(R.id.file_list);
        main_drawer_layout = findViewById(R.id.main_drawer_layout);
        rv_collection_list = findViewById(R.id.collection_list);
        //
        fileListViewAdapter = new FileListViewAdapter(this);
        fileListViewAdapter.setOnItemClickListener((item, action) -> {
            // anim
            switch (action) {
                case NEXT:
                    image_switcher.setInAnimation(image_switcher_rin);
                    image_switcher.setOutAnimation(image_switcher_rout);
                    break;
                case MOVE_GET_NEXT:
                    image_switcher.setInAnimation(image_switcher_rin);
                    image_switcher.setOutAnimation(image_switcher_down_p1);
                    break;
                case REMOVE_GET_NEXT:
                    image_switcher.setInAnimation(image_switcher_rin);
                    image_switcher.setOutAnimation(image_switcher_up_p1);
                    break;
                case PREVIOUS:
                    image_switcher.setInAnimation(image_switcher_lin);
                    image_switcher.setOutAnimation(image_switcher_lout);
                    break;
                case MOVE_GET_PREVIOUS:
                    image_switcher.setInAnimation(image_switcher_lin);
                    image_switcher.setOutAnimation(image_switcher_down_p1);
                    break;
                case REMOVE_GET_PREVIOUS:
                    image_switcher.setInAnimation(image_switcher_lin);
                    image_switcher.setOutAnimation(image_switcher_up_p1);
                    break;
            }
            // image
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
        rv_file_list.setAdapter(fileListViewAdapter);
        //
        collectionListViewAdapter = new CollectionListViewAdapter(this);
        collectionListViewAdapter.setOnCollectionChangedListener((collection, item) -> {
            fileListViewAdapter.setCollection(collection, item);
            setTitle(collection.getName());
            main_drawer_layout.closeDrawers();
            if (!collection.equals(collectionListViewAdapter.getBaseDirectory())) {
                optMenu.findItem(R.id.delete_collection).setEnabled(true);
                optMenu.findItem(R.id.rename_collection).setEnabled(true);
            } else {
                optMenu.findItem(R.id.delete_collection).setEnabled(false);
                optMenu.findItem(R.id.rename_collection).setEnabled(false);
            }
        });
        rv_collection_list.setAdapter(collectionListViewAdapter);
        //
        image_switcher_lin = AnimationUtils.loadAnimation(this, R.anim.image_switcher_lin);
        image_switcher_lout = AnimationUtils.loadAnimation(this, R.anim.image_switcher_lout);
        image_switcher_rin = AnimationUtils.loadAnimation(this, R.anim.image_switcher_rin);
        image_switcher_rout = AnimationUtils.loadAnimation(this, R.anim.image_switcher_rout);
        image_switcher_down_p1 = AnimationUtils.loadAnimation(this, R.anim.image_switcher_down);
        //image_switcher_down_p2 = AnimationUtils.loadAnimation(this, R.anim.image_switcher_down_p2);
        image_switcher_up_p1 = AnimationUtils.loadAnimation(this, R.anim.image_switcher_up);
        //image_switcher_up_p2 = AnimationUtils.loadAnimation(this, R.anim.image_switcher_up_p2);
        //
        image_switcher.setFactory(() -> {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(0xFFFFFFFF);
            return imageView;
        });
        //
        selectToCollectionIntent = new Intent(this, SelectCollectionActivity.class);
        //
        directoryChooser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = result.getData().getExtras();
                File baseDirectory = (File) bundle.get(DirectoryChooserActivity.KEY_SELECTED_FILE);
                collectionListViewAdapter.setBaseDirectory(baseDirectory);
                optMenu.findItem(R.id.create_collection).setEnabled(true);
                optMenu.findItem(R.id.delete_collection).setEnabled(false);
                optMenu.findItem(R.id.rename_collection).setEnabled(false);
            }
        });
        //
        selectToCollection = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = result.getData().getExtras();
                File toCollection = (File) bundle.get(SelectCollectionActivity.KEY_SELECTED_COLLECTION);
                File selectedItem = fileListViewAdapter.getCurrentItem();
                File toFile = new File(toCollection.getPath() + File.separatorChar + selectedItem.getName());
                int counter = 0;
                while (toFile.exists()) {
                    toFile = new File(toCollection.getPath() + File.separatorChar + counter + '_' + selectedItem.getName());
                    counter++;
                }
                if (selectedItem.renameTo(toFile)) {
                    fileListViewAdapter.removeCurrentItem(false);
                }
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
        //
        deleteFileAlert = new AlertDialog.Builder(this);
        //
        File session = new File(getExternalFilesDir(null).getPath()
                + File.separatorChar + SESSION_FILE_NAME);
        if (session.exists()) {
            try (BufferedReader bw = new BufferedReader(new InputStreamReader(
                    new FileInputStream(session), StandardCharsets.UTF_8))) {
                String baseDirectory = bw.readLine();
                String collectionName = bw.readLine();
                String fileName = bw.readLine();
                File baseFile = new File(baseDirectory);
                if (baseFile.exists()) {
                    File collectionFile = new File(baseFile.getName().equalsIgnoreCase(collectionName) ?
                            baseDirectory : baseDirectory + File.separatorChar + collectionName);
                    if (!collectionFile.exists()) {
                        collectionFile = null;
                    }
                    File itemFile = new File(collectionFile.getPath() + File.separatorChar + fileName);
                    if (!itemFile.exists()) {
                        itemFile = null;
                    }
                    collectionListViewAdapter.init(baseFile, collectionFile, itemFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        optMenu = menu;
        if (collectionListViewAdapter.getItemCount() > 0) {
            menu.findItem(R.id.create_collection).setEnabled(true);
            if (!collectionListViewAdapter.getCurrentCollection().equals(
                    collectionListViewAdapter.getBaseDirectory())) {
                menu.findItem(R.id.delete_collection).setEnabled(true);
                menu.findItem(R.id.rename_collection).setEnabled(true);
            }
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_folder:
                Intent chooseFile = new Intent(this, DirectoryChooserActivity.class);
                directoryChooser.launch(chooseFile);
                break;
            case R.id.create_collection:
                if (collectionListViewAdapter.getItemCount() > 0) {
                    InputAlertDialog inputAlertDialog = new InputAlertDialog(this);
                    inputAlertDialog.setTitle("Создание");
                    inputAlertDialog.setInputAlertDialogActions(new InputAlertDialog.InputAlertDialogActions() {
                        @Override
                        public String OnValidation(String text) {
                            if ("".equals(text)) {
                                return "Введите название коллекции";
                            }
                            Pattern checkSpecPathSim = Pattern.compile("[<>:\"/\\\\|?*]");
                            if (checkSpecPathSim.matcher(text).matches()) {
                                return "Название коллекции содержит запрещенные символы (< > : \" / \\ | ? *)";
                            }
                            String basePath = collectionListViewAdapter.getBaseDirectory().getPath();
                            File checkDirectory = new File(basePath + File.separatorChar + text);
                            if (checkDirectory.exists()) {
                                return "Коллекция с таким названием уже существует";
                            }
                            return null;
                        }

                        @Override
                        public void OnSuccess(String text) {
                            String basePath = collectionListViewAdapter.getBaseDirectory().getPath();
                            File collection = new File(basePath + File.pathSeparator + text);
                            if (collection.mkdir()) {
                                collectionListViewAdapter.add(collection);
                            }
                        }
                    });
                    inputAlertDialog.show();
                    break;
                }
            case R.id.delete_collection:
                AlertDialog.Builder confirmDeleteColl = new AlertDialog.Builder(this)
                        .setMessage("Вы действительно хотите удалить коллекцию "
                                + collectionListViewAdapter.getCurrentCollection().getName()
                                + " со всеми ее файлами?")
                        .setPositiveButton("Да", (dialogInterface, id) -> {
                            collectionListViewAdapter.deleteCurrent();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Нет", (dialogInterface, id) -> {
                            dialogInterface.dismiss();
                        });
                confirmDeleteColl.show();
                break;
            case R.id.rename_collection:
                Activity _this = this;
                InputAlertDialog renameCollection = new InputAlertDialog(this);
                renameCollection.setInputAlertDialogActions(new InputAlertDialog.InputAlertDialogActions() {
                    @Override
                    public String OnValidation(String text) {
                        if ("".equals(text)) {
                            return "Введите название коллекции";
                        }
                        Pattern checkSpecPathSim = Pattern.compile("[<>:\"/\\\\|?*]");
                        if (checkSpecPathSim.matcher(text).matches()) {
                            return "Название коллекции содержит запрещенные символы (< > : \" / \\ | ? *)";
                        }
                        String basePath = collectionListViewAdapter.getBaseDirectory().getPath();
                        File checkDirectory = new File(basePath + File.separatorChar + text);
                        if (checkDirectory.exists()) {
                            return "Коллекция с таким названием уже существует";
                        }
                        return null;
                    }

                    @Override
                    public void OnSuccess(String text) {
                        if (collectionListViewAdapter.renameCurrent(text)) {
                            _this.setTitle(text);
                        }
                    }
                });
                renameCollection.show();
                break;
        }
        return true;
    }

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            float sizeX = Math.abs(motionEvent.getX() - motionEvent1.getX());
            float sizeY = Math.abs(motionEvent.getY() - motionEvent1.getY());
            if (sizeX > sizeY && sizeX > 50.0d) {
                if (motionEvent.getX() > motionEvent1.getX()) {
                    //image_switcher.setInAnimation(image_switcher_rin);
                    //image_switcher.setOutAnimation(image_switcher_rout);
                    fileListViewAdapter.next();
                } else {
                    //image_switcher.setInAnimation(image_switcher_lin);
                    //image_switcher.setOutAnimation(image_switcher_lout);
                    fileListViewAdapter.previous();
                }
            } else if (sizeY > 50.0d && fileListViewAdapter.getItemCount() > 0) {
                if (motionEvent.getY() < motionEvent1.getY()) {
                    selectToCollectionIntent.putExtra(SelectCollectionActivity.KEY_BASE_DIRECTORY,
                            collectionListViewAdapter.getBaseDirectory());
                    selectToCollectionIntent.putExtra(SelectCollectionActivity.KEY_SELECTED_COLLECTION,
                            collectionListViewAdapter.getCurrentCollection());
                    selectToCollection.launch(selectToCollectionIntent);
                } else {
                    File currentItem = fileListViewAdapter.getCurrentItem();
                    deleteFileAlert.setMessage("Вы действительно хотите удалить файл " + currentItem.getName() + "?")
                            .setPositiveButton("Да", (dialogInterface, id) -> {
                                if (currentItem.delete()) {
                                    fileListViewAdapter.removeCurrentItem(true);
                                }
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("Нет", (dialogInterface, id) -> {
                                dialogInterface.dismiss();
                            });
                    deleteFileAlert.show();
                }
            }
            return true;
        }
    }

    @Override
    protected void onStop() {
        if (collectionListViewAdapter.getItemCount() > 0) {
            File session = new File(getExternalFilesDir(null).getPath()
                    + File.separatorChar + SESSION_FILE_NAME);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(session, false), StandardCharsets.UTF_8))) {
                bw.write(collectionListViewAdapter.getBaseDirectory().getPath());
                bw.newLine();
                bw.write(collectionListViewAdapter.getCurrentCollection().getName());
                bw.newLine();
                bw.write(fileListViewAdapter.getCurrentItem().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }
}