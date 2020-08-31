package com.example.videotoimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseVideoFolderActivity extends AppCompatActivity {
    private ArrayList<VideoFolder> listFolder;
    RecyclerView rcVideoFolder;
    VideoFolderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_video_folder);

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

        rcVideoFolder = (RecyclerView) findViewById(R.id.rcVideoFolder);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File f = new File(path);//converted string object to file
        listFolder = new ArrayList<VideoFolder>();
        getAllFolder(f,f.getName());
        rcVideoFolder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcVideoFolder.setItemAnimator(new DefaultItemAnimator());
        adapter = new VideoFolderAdapter(this, listFolder);
        rcVideoFolder.setAdapter(adapter);

    }

    void getAllFolder(File file, String name) {
        File list[] = file.listFiles();
        boolean folderFound = false;
        File mfile = null;
        String directoryName = "";
        int n = 0;
        for (int i = 0; i < list.length; i++) {
            mfile = new File(file, list[i].getName());
            if (mfile.isDirectory()) {
                getAllFolder(mfile, directoryName);
            } else {
                if (list[i].getName().toLowerCase(Locale.getDefault()).endsWith(".mp4")) {
                    folderFound = true;
                    n++;
                }
            }
        }
        if (folderFound) {
            listFolder.add(new VideoFolder(file.getName(), n));
        }
    }
}