package com.example.videotoimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.videotoimages.adapter.VideoFolderAdapter;
import com.example.videotoimages.model.VideoFolder;

import java.io.File;
import java.util.ArrayList;

public class ChooseVideoFolderActivity extends AppCompatActivity {
    private ArrayList<VideoFolder> listFolder;
    RecyclerView rcVideoFolder;
    VideoFolderAdapter adapter;
    public static final int PERMISSION_READ = 0;
    String path = "";
    File f = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_video_folder);
        setTitle("Choose Folder");

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


        if (checkPermission()) {
            new LoadFolder().execute();
        }

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
                if (list[i].getName().endsWith(".mp4") || list[i].getName().endsWith(".avi")) {
                    folderFound = true;
                    n++;
                }
            }
        }
        if (folderFound) {
            listFolder.add(new VideoFolder(file.getName(), n));
        }
    }

    public class LoadFolder extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            f = new File(path);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listFolder = new ArrayList<VideoFolder>();
            getAllFolder(f, f.getName());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            rcVideoFolder.setLayoutManager(new LinearLayoutManager(ChooseVideoFolderActivity.this));
            rcVideoFolder.setItemAnimator(new DefaultItemAnimator());
            adapter = new VideoFolderAdapter(ChooseVideoFolderActivity.this, listFolder);
            rcVideoFolder.setAdapter(adapter);
        }
    }

    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        new LoadFolder().execute();
                    }
                }
            }
        }
    }
}