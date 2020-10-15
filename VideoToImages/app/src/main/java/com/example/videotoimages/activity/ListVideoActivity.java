package com.example.videotoimages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.videotoimages.R;
import com.example.videotoimages.adapter.VideoAdapter;
import com.example.videotoimages.model.Video;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

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
        //getVideos();
        getAllVideos();
    }

    private void getAllVideos() {
        File photoFolder = new File(Environment.getExternalStorageDirectory() + "/" + folderName + "/");
        if (photoFolder.exists()) {
            File[] allFiles = photoFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".mp4") || name.endsWith(".avi"));
                }
            });
            for (int i = 0; i < allFiles.length; i++) {
                Video video = new Video();
                try {
                    Bitmap bitmap = getBitmapFromVideo(allFiles[i].getAbsolutePath());
                    if (bitmap != null) {
                        video.setBitmap(bitmap);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.d("TAG", throwable.getMessage());
                }
                video.setDuration((int) getDur(allFiles[i].getAbsolutePath()));
                video.setPath(allFiles[i].getAbsolutePath());
                video.setName(allFiles[i].getName());
                videoArrayList.add(video);
            }
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

    //    public void getVideos() {
//        ContentResolver contentResolver = getContentResolver();
//        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Video.Media.DATA + " like?";
//        String[] selectionArgs = new String[]{"%" + folderName + "%"};
//
//        Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
//                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
//                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//                String thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//                Video videoModel = new Video();
//                videoModel.setName(title);
//                videoModel.setPath(data);
//                videoModel.setThumb(thumb);
//                videoModel.setDuration(duration);
//                videoArrayList.add(videoModel);
//            } while (cursor.moveToNext());
//        }
//
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
//
//    }
    public static Bitmap getBitmapFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception: " + e);
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public long getDur(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return timeInmillisec;
    }

}