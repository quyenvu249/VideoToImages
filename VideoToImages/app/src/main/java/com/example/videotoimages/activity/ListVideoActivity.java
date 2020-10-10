package com.example.videotoimages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;

import com.example.videotoimages.R;
import com.example.videotoimages.adapter.VideoAdapter;
import com.example.videotoimages.model.Video;

import java.util.ArrayList;

public class ListVideoActivity extends AppCompatActivity {
    public static ArrayList<Video> videoArrayList;
    RecyclerView recyclerView;
    String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        setTitle("List Video");

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

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("bundle");
        folderName = b.getString("folderName");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoList();
            }
        }, 500);
    }

    public void videoList() {
        recyclerView = (RecyclerView) findViewById(R.id.rcVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        videoArrayList = new ArrayList<>();
        getVideos();
    }

//    private void getAllVideos() {
//        File photoFolder = new File(Environment.getExternalStorageDirectory() + "/" + folderName + "/");
//        if (photoFolder.exists()) {
//            File[] allFiles = photoFolder.listFiles(new FilenameFilter() {
//                public boolean accept(File dir, String name) {
//                    return (name.endsWith(".mp4") || name.endsWith(".avi"));
//                }
//            });
//            for (int i = 0; i < allFiles.length; i++) {
//                Video video = new Video(allFiles[i].getName(), allFiles[i].getAbsolutePath(), allFiles[i].getAbsolutePath(),allFiles[i].);
//                videoArrayList.add(video);
//            }
//        }
//        VideoAdapter adapter = new VideoAdapter(this, videoArrayList);
//        recyclerView.setAdapter(adapter);
//
//        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int pos, View v) {
//                Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
//                intent.putExtra("pos", pos);
//                startActivity(intent);
//            }
//        });
//    }

    public void getVideos() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};

        Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Video videoModel = new Video();
                videoModel.setName(title);
                videoModel.setPath(data);
                videoModel.setThumb(thumb);
                videoModel.setDuration(duration);
                videoArrayList.add(videoModel);
            } while (cursor.moveToNext());
        }

        VideoAdapter adapter = new VideoAdapter(this, videoArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
                intent.putExtra("pos", pos);
                startActivity(intent);
            }
        });

    }

}