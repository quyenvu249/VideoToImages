package com.example.videotoimages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.videotoimages.R;
import com.example.videotoimages.adapter.PhotosPreviewAdapter;
import com.example.videotoimages.model.CreatedPhotos;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayVideoActivity extends AppCompatActivity {
    LinearLayout layoutSnap;
    Toolbar toolbar;
    RecyclerView rcPreview;
    CheckedTextView ctvQuickCapture, ctvTimeCapture;
    VideoView videoPlaying;
    TextView tvTimePlaying, tvTotalTime, tvName, tvTimeToSnap;
    SeekBar seekBar;
    SimpleDateFormat simpleTime = new SimpleDateFormat("mm:ss");
    ImageView btnPlay, icCamera, btnPause;
    RadioButton rbSnap;
    Button btnDone, btnStop;
    FileOutputStream fileOutputStream;
    MediaMetadataRetriever mediaMetadataRetriever;
    String path;
    final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/screenshots/";
    PhotosPreviewAdapter adapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREFERENCES_NAME = "Settings";
    String file_format, quality, size;
    ArrayList<CreatedPhotos> arrayList;
    Bitmap bmFrame;
    int currentPosition;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        setTitle("Playing Video");

        AnhXa();

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

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        file_format = sharedPreferences.getString("file_format", "JPG");
        quality = sharedPreferences.getString("quality", "High");
        size = sharedPreferences.getString("size", "1x");

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("Path");
        path = b.getString("videoPath");
        videoPlaying.setVideoPath(b.getString("videoPath"));

        prepareVideo();
        UpdateTimeVideo();

        tvName.setText(b.getString("videoName"));
        tvTotalTime.setText(simpleTime.format(b.getInt("videoDur")) + "");

        seekBar.setMax(b.getInt("videoDur"));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoPlaying.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlaying.seekTo(seekBar.getProgress());
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.INVISIBLE);
                videoPlaying.start();
                btnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPlay.setVisibility(View.VISIBLE);
                        btnPause.setVisibility(View.INVISIBLE);
                        videoPlaying.pause();
                    }
                });
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setVisibility(View.VISIBLE);
                videoPlaying.pause();
            }
        });

        arrayList = new ArrayList<>();
        ctvQuickCapture.setChecked(true);
        ctvQuickCapture.setTextColor(Color.BLUE);
        if (ctvQuickCapture.isChecked()) {
            icCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnPlay.setVisibility(View.INVISIBLE);
                    quickCapture();
                }
            });
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PlayVideoActivity.this, GalleryActivity.class));
                }
            });

        }

        ctvTimeCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctvTimeCapture.setChecked(true);
                ctvTimeCapture.setTextColor(Color.BLUE);
                ctvQuickCapture.setChecked(false);
                ctvQuickCapture.setTextColor(Color.BLACK);
                tvTimeToSnap.setVisibility(View.VISIBLE);
                rbSnap.setVisibility(View.VISIBLE);
                rbSnap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayVideoActivity.this);
                        View view = LayoutInflater.from(PlayVideoActivity.this).inflate(R.layout.dialog_time_snap, null);
                        final TextInputEditText edTimeSnap;
                        edTimeSnap = view.findViewById(R.id.edTimeSnap);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (edTimeSnap.getText().toString().equals("")) {
                                    Toast.makeText(PlayVideoActivity.this, "Please enter value first", Toast.LENGTH_SHORT).show();
                                } else {
                                    tvTimeToSnap.setText(edTimeSnap.getText().toString());
                                }
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                rbSnap.setChecked(false);
                            }
                        });
                        alertDialog.setView(view);
                        alertDialog.create();
                        alertDialog.show();
                    }
                });
                icCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        capture();
                    }
                });
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Done", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PlayVideoActivity.this, GalleryActivity.class));

                    }
                });
            }
        });
        ctvQuickCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctvQuickCapture.setChecked(true);
                ctvQuickCapture.setTextColor(Color.BLUE);
                ctvTimeCapture.setChecked(false);
                ctvTimeCapture.setTextColor(Color.BLACK);
                tvTimeToSnap.setVisibility(View.INVISIBLE);
                rbSnap.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void AnhXa() {
        layoutSnap = findViewById(R.id.layoutSnap);
        tvTimeToSnap = findViewById(R.id.tvTimeToSnap);
        toolbar = findViewById(R.id.toolbar);
        ctvQuickCapture = findViewById(R.id.ctvQuickCapture);
        ctvTimeCapture = findViewById(R.id.ctvTimeCapture);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        videoPlaying = findViewById(R.id.videoPlaying);
        tvTimePlaying = findViewById(R.id.tvTimePlay);
        tvName = findViewById(R.id.tvName);
        seekBar = findViewById(R.id.seekbar);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        rbSnap = findViewById(R.id.rbSnap);
        icCamera = findViewById(R.id.icCamera);
        btnDone = findViewById(R.id.btnDone);
        btnStop = findViewById(R.id.btnStop);
        rcPreview = findViewById(R.id.rcPreview);
    }

    private void UpdateTimeVideo() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(videoPlaying.getCurrentPosition());
                tvTimePlaying.setText(simpleTime.format(videoPlaying.getCurrentPosition()));
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    private void prepareVideo() {
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(PlayVideoActivity.this, Uri.parse(path));
        Log.d("VideoPath", path);
        videoPlaying.setVideoPath(path);
        seekBar.setMax(videoPlaying.getDuration());
        videoPlaying.requestFocus();
    }

    private void quickCapture() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = videoPlaying.getCurrentPosition();
                Bitmap imageBitmap;
                int position = currentPosition * 1000;
                imageBitmap = mediaMetadataRetriever.getFrameAtTime(position);
                if (imageBitmap == null) {
                    Toast.makeText(PlayVideoActivity.this, "Null", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        final File file = new File(SCREENSHOTS_LOCATIONS);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (file_format.equals("JPG")) {
                            fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".jpg");
                            if (fileOutputStream != null) {
                                switch (quality.trim()) {
                                    case "Best":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Very High":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "High":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Medium":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Low":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                }
                            }
                        } else {
                            fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".png");
                            if (fileOutputStream != null) {
                                switch (quality.trim()) {
                                    case "Best":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Very High":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "High":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 60, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Medium":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                    case "Low":
                                        if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, 20, fileOutputStream)) {
                                            Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                }
                            }
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        CreatedPhotos createdPhotos = new CreatedPhotos(imageBitmap, fileOutputStream + "", file.getAbsolutePath(), Integer.parseInt(file.length() + ""), false);
                        arrayList.add(createdPhotos);
                        adapter = new PhotosPreviewAdapter(PlayVideoActivity.this, arrayList);
                        rcPreview.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        rcPreview.setAdapter(adapter);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 200);
    }

    private void capture() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPosition = Integer.parseInt(tvTimeToSnap.getText().toString()) * 1000;
                while (currentPosition < videoPlaying.getDuration()) {
                    currentPosition = currentPosition + Integer.parseInt(tvTimeToSnap.getText().toString()) * 1000;
                    int position = currentPosition * 1000;
                    bmFrame = mediaMetadataRetriever.getFrameAtTime(position);
                    if (bmFrame != null) {
                        try {
                            File file = new File(SCREENSHOTS_LOCATIONS);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (file_format.equals("JPG")) {
                                fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".jpg");
                                if (fileOutputStream != null) {
                                    switch (quality.trim()) {
                                        case "Best":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Very High":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "High":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Medium":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Low":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                }
                            } else {
                                fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".png");
                                if (fileOutputStream != null) {
                                    switch (quality.trim()) {
                                        case "Best":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Very High":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "High":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.PNG, 60, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Medium":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                        case "Low":
                                            if (!bmFrame.compress(Bitmap.CompressFormat.PNG, 20, fileOutputStream)) {
                                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                }
                            }
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            CreatedPhotos createdPhotos = new CreatedPhotos(bmFrame, fileOutputStream + "", file.getAbsolutePath(), Integer.parseInt(file.length() + ""), false);
                            arrayList.add(createdPhotos);
                            adapter = new PhotosPreviewAdapter(PlayVideoActivity.this, arrayList);
                            rcPreview.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rcPreview.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 100);

    }
}