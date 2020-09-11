package com.example.videotoimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
    ImageView imgPreview, btnPlay, icCamera;
    RadioButton rbSnap;
    Button btnDone;
    FileOutputStream fileOutputStream;
    MediaMetadataRetriever mediaMetadataRetriever;
    String path;
    final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/screenshots/";
    ArrayList<CreatedPhotos> arrayList;
    PhotosPreviewAdapter adapter;

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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlaying.seekTo(seekBar.getProgress());
            }
        });
        videoPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlaying.pause();
                btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                videoPlaying.start();
            }
        });

        ctvQuickCapture.setChecked(true);
        ctvQuickCapture.setTextColor(Color.BLUE);
        icCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList = new ArrayList<>();
                btnPlay.setVisibility(View.INVISIBLE);
                int currentPosition = videoPlaying.getCurrentPosition();
                Bitmap bmFrame;
                int position = currentPosition * 1000;
                bmFrame = mediaMetadataRetriever.getFrameAtTime(position);
                if (bmFrame == null) {
                    Toast.makeText(PlayVideoActivity.this, "Null", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        final File file = new File(SCREENSHOTS_LOCATIONS);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".jpg");
                        if (fileOutputStream != null) {
                            if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PlayVideoActivity.this, "Đã lưu", Toast.LENGTH_SHORT).show();
                            }
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            CreatedPhotos createdPhotos;
                            File[] allfiles = file.listFiles();
                            for (int i = 0; i < allfiles.length; i++) {
                                createdPhotos = new CreatedPhotos(bmFrame, allfiles[i].getName(), allfiles[i].getAbsolutePath(), Integer.parseInt(allfiles[i].length() + ""));
                                arrayList.add(createdPhotos);
                            }
                            adapter = new PhotosPreviewAdapter(PlayVideoActivity.this, arrayList);
                            rcPreview.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            rcPreview.setAdapter(adapter);

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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
                        int currentPosition = videoPlaying.getCurrentPosition();
                        Bitmap bmFrame;
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
                                    fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".jpg");
                                    if (fileOutputStream != null) {
                                        if (!bmFrame.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
                                            Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                                        }
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlayVideoActivity.this, GalleryActivity.class));
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
        rbSnap = findViewById(R.id.rbSnap);
        icCamera = findViewById(R.id.icCamera);
        btnDone = findViewById(R.id.btnDone);
        //imgPreview = findViewById(R.id.imgPreview);
        rcPreview = findViewById(R.id.rcPreview);
    }

    private void UpdateTimeVideo() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        videoPlaying.requestFocus();
        videoPlaying.start();
    }

}