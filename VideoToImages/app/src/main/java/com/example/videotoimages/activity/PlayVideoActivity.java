package com.example.videotoimages.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.videotoimages.R;
import com.example.videotoimages.adapter.PhotosPreviewAdapter;
import com.example.videotoimages.adapter.VideoAdapter;
import com.example.videotoimages.model.CreatedPhotos;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayVideoActivity extends AppCompatActivity {
    LinearLayout layoutSnap, layoutProgress;
    Toolbar toolbar;
    RecyclerView rcPreview;
    CheckedTextView ctvQuickCapture, ctvTimeCapture;
    VideoView videoPlaying;
    TextView tvTimePlaying, tvTotalTime, tvTimeToSnap;
    SeekBar seekBar;
    DateFormat simpleTime = new SimpleDateFormat("mm:ss");
    ImageView btnPlay, icCamera, btnPause, icDone;
    RadioButton rbSnap;
    Button btnStop;
    FileOutputStream fileOutputStream;
    MediaMetadataRetriever mediaMetadataRetriever;
    String path;
    final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/screenshots/";
    PhotosPreviewAdapter adapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREFERENCES_NAME = "Settings";
    String file_format, quality, size;
    int qualityImage;
    ArrayList<CreatedPhotos> arrayList;
    Bitmap bmFrame;
    int currentPosition, timeToCapture;
    CreatedPhotos createdPhotos;
    int newCurrentPos, imageCount;
    TimeCapture timeCapture;
    ProgressBar progressBar;
    TextView tvProgress;
    int value = 0;


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

        getQuality(quality);

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("Path");
        path = b.getString("videoPath");
        videoPlaying.setVideoPath(b.getString("videoPath"));

        prepareVideo();
        UpdateTimeVideo();

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

        layoutSnap.setVisibility(View.INVISIBLE);
        layoutProgress.setVisibility(View.INVISIBLE);
        ctvQuickCapture.setChecked(true);
        ctvQuickCapture.setTextColor(Color.RED);
        ctvQuickCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctvQuickCapture.setChecked(true);
                ctvQuickCapture.setTextColor(Color.RED);
                ctvTimeCapture.setChecked(false);
                ctvTimeCapture.setTextColor(Color.BLACK);
                layoutSnap.setVisibility(View.INVISIBLE);
                layoutProgress.setVisibility(View.INVISIBLE);
            }
        });
        ctvTimeCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctvTimeCapture.setChecked(true);
                ctvTimeCapture.setTextColor(Color.RED);
                ctvQuickCapture.setChecked(false);
                ctvQuickCapture.setTextColor(Color.BLACK);
                layoutSnap.setVisibility(View.VISIBLE);
                if (ctvTimeCapture.isChecked()) {
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
                            videoPlaying.pause();
                            layoutProgress.setVisibility(View.VISIBLE);
//                            new TimeCapture().execute();
                            newCurrentPos = videoPlaying.getCurrentPosition();
                            timeToCapture = Integer.parseInt(tvTimeToSnap.getText().toString());
                            imageCount = (videoPlaying.getDuration() - newCurrentPos) / (timeToCapture * 1000);
                            timeCapture = new TimeCapture();
                            timeCapture.execute();

                            Log.d("time", imageCount + "");
                            btnStop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (timeCapture.isCancelled()) {
                                        timeCapture = new TimeCapture();
                                        timeCapture.execute();
                                        btnStop.setText("Stop");
                                    } else {
                                        timeCapture.cancel(true);
                                        btnStop.setText("Continue");
                                    }
                                }
                            });
                        }
                    });
                    icDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PlayVideoActivity.this, GalleryActivity.class));
                        }
                    });
                }
            }
        });
        if (ctvQuickCapture.isChecked()) {
            icCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnPlay.setVisibility(View.INVISIBLE);
                    new quickCap().execute();
                }
            });
            icDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PlayVideoActivity.this, GalleryActivity.class));
                }
            });
        }

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
        layoutProgress = findViewById(R.id.layoutProgress);
        seekBar = findViewById(R.id.seekbar);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        rbSnap = findViewById(R.id.rbSnap);
        icCamera = findViewById(R.id.icCamera);
        //btnDone = findViewById(R.id.btnDone);
        icDone = findViewById(R.id.icDone);
        btnStop = findViewById(R.id.btnStop);
        rcPreview = findViewById(R.id.rcPreview);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
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

    private void getQuality(String quality) {
        if (quality.equals("Best")) {
            qualityImage = 100;
        } else if (quality.equals("Very High")) {
            qualityImage = 80;
        } else if (quality.equals("High")) {
            qualityImage = 60;
        } else if (quality.equals("Medium")) {
            qualityImage = 40;
        } else {
            qualityImage = 20;
        }
    }

    private void prepareVideo() {
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(PlayVideoActivity.this, Uri.parse(path));
        Log.d("VideoPath", path);
        videoPlaying.setVideoPath(path);
        seekBar.setMax(videoPlaying.getDuration());
        videoPlaying.requestFocus();
    }

    public class quickCap extends AsyncTask<Void, ArrayList<CreatedPhotos>, ArrayList<CreatedPhotos>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentPosition = videoPlaying.getCurrentPosition();
        }

        @Override
        protected void onPostExecute(ArrayList<CreatedPhotos> createdPhotos) {
            super.onPostExecute(createdPhotos);
            rcPreview.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
            rcPreview.setAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(ArrayList<CreatedPhotos>... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<CreatedPhotos> doInBackground(Void... voids) {
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
                            if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, qualityImage, fileOutputStream)) {
                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".png");
                        if (fileOutputStream != null) {
                            if (!imageBitmap.compress(Bitmap.CompressFormat.PNG, qualityImage, fileOutputStream)) {
                                Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    createdPhotos = new CreatedPhotos(imageBitmap, fileOutputStream + "", file.getAbsolutePath(), Integer.parseInt(file.length() + ""), false);
                    arrayList.add(createdPhotos);
                    adapter = new PhotosPreviewAdapter(PlayVideoActivity.this, arrayList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return arrayList;
        }
    }

    public class TimeCapture extends AsyncTask<Void, Integer, ArrayList<CreatedPhotos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMax(imageCount);
            tvProgress.setText("0/" + (imageCount + 1));
            rcPreview.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        }

        @Override
        protected void onPostExecute(ArrayList<CreatedPhotos> createdPhotos) {
            super.onPostExecute(createdPhotos);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvProgress.setText(values[0] + "/" + (imageCount + 1));
            progressBar.setProgress(values[0]);
            if (values[0] == imageCount + 1) {
                btnStop.setVisibility(View.INVISIBLE);
                tvProgress.setText("Done");
            }
            rcPreview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(ArrayList<CreatedPhotos> createdPhotos) {
            super.onCancelled(createdPhotos);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @SuppressLint("WrongThread")
        @Override
        protected ArrayList<CreatedPhotos> doInBackground(Void... voids) {
            int position = timeToCapture * 1000;
            Bitmap bitmap;
            for (int i = newCurrentPos; i < videoPlaying.getDuration(); i += position) {
                bitmap = mediaMetadataRetriever.getFrameAtTime(newCurrentPos * 1000);
                if (bitmap == null) {
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
                                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, qualityImage, fileOutputStream)) {
                                    Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            fileOutputStream = new FileOutputStream(SCREENSHOTS_LOCATIONS + "photo" + System.currentTimeMillis() + ".png");
                            if (fileOutputStream != null) {
                                if (!bitmap.compress(Bitmap.CompressFormat.PNG, qualityImage, fileOutputStream)) {
                                    Toast.makeText(PlayVideoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        createdPhotos = new CreatedPhotos(bitmap, fileOutputStream + "", file.getAbsolutePath(), Integer.parseInt(file.length() + ""), false);
                        arrayList.add(0, createdPhotos);
                        value += 1;
                        newCurrentPos += position;
                        adapter = new PhotosPreviewAdapter(PlayVideoActivity.this, arrayList);
                        publishProgress(value);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (timeCapture.isCancelled()) {
                    break;
                }
            }

            return arrayList;
        }
    }

}