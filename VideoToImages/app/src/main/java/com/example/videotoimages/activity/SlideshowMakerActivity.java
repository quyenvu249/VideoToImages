package com.example.videotoimages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.GridView;

import com.example.videotoimages.R;
import com.example.videotoimages.adapter.CreatedPhotosAdapter;
import com.example.videotoimages.adapter.ImageFolderAdapter;
import com.example.videotoimages.adapter.ItemsSelectedAdapter;
import com.example.videotoimages.model.CreatedPhotos;
import com.example.videotoimages.model.ImageFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class SlideshowMakerActivity extends AppCompatActivity {
    RecyclerView rcSl, rcItemsSelected;
    ArrayList<ImageFolder> listFolder;
    ArrayList<CreatedPhotos> listPhotos;
    CheckBox cbChecked;
    String path;
    ImageFolderAdapter adapter;
    CreatedPhotosAdapter createdPhotosAdapter;
    GridView grPhotos;
    File f;
    public static final int SELECT_PICTURES = 99;
    ArrayList<CreatedPhotos> choosedImages;
    int count = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Slideshow Maker");
        setContentView(R.layout.activity_slideshow_maker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rcSl = findViewById(R.id.rcSl);
        rcItemsSelected = findViewById(R.id.rcItemsSelected);
        //cbChecked = findViewById(R.id.circle);
        grPhotos = findViewById(R.id.grImages);
        listFolder = new ArrayList<>();
        choosedImages = new ArrayList<>();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                path = Environment.getExternalStorageDirectory().toString();
                f = new File(path);
                getAllFolder(f, f.getName());
                rcSl.setLayoutManager(new LinearLayoutManager(SlideshowMakerActivity.this, LinearLayoutManager.HORIZONTAL, false));
                rcSl.setItemAnimator(new DefaultItemAnimator());
                adapter = new ImageFolderAdapter(SlideshowMakerActivity.this, listFolder);
                rcSl.setAdapter(adapter);

                adapter.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos, View v) {
                        listPhotos = new ArrayList<>();
                        File photoFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" + listFolder.get(pos).getImageFolderName() + "/");
                        if (photoFolder.exists()) {
                            File[] allFiles = photoFolder.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                                }
                            });
                            for (int i = 0; i < allFiles.length; i++) {
                                CreatedPhotos createdPhotos = new CreatedPhotos(BitmapFactory.decodeFile(allFiles[i].getAbsolutePath(), new BitmapFactory.Options()), allFiles[i].getName(), allFiles[i].getAbsolutePath(), Integer.parseInt(allFiles[i].length() + ""),false);
                                listPhotos.add(createdPhotos);
                            }

                        }
                        createdPhotosAdapter = new CreatedPhotosAdapter(SlideshowMakerActivity.this, listPhotos);
                        grPhotos.setAdapter(createdPhotosAdapter);
                        grPhotos.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
                        grPhotos.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                            @Override
                            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                                count++;
                                mode.setTitle(count + " items selected");
                                choosedImages.add(listPhotos.get(position));
                                ItemsSelectedAdapter itemsSelectedAdapter = new ItemsSelectedAdapter(SlideshowMakerActivity.this, choosedImages);
                                rcItemsSelected.setLayoutManager(new LinearLayoutManager(SlideshowMakerActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                rcItemsSelected.setItemAnimator(new DefaultItemAnimator());
                                rcItemsSelected.setAdapter(itemsSelectedAdapter);
                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.add_images, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                return false;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {

                            }
                        });
                    }
                });
            }
        }, 1000);


    }

    private void getAllFolder(File file, String name) {
        File list[] = file.listFiles();
        boolean folderFound = false;
        File mfile;
        String directoryName = "";
        int n = 0;
        for (int i = 0; i < list.length; i++) {
            mfile = new File(file, list[i].getName());
            if (mfile.isDirectory()) {
                getAllFolder(mfile, directoryName);
            } else {
                if (list[i].getName().endsWith(".jpg") || list[i].getName().endsWith(".jpeg") || list[i].getName().endsWith(".png")) {
                    folderFound = true;
                    n++;
                }
            }
        }
        if (folderFound) {
            listFolder.add(new ImageFolder(file.getName(), n));
        }
    }
}