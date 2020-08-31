package com.example.videotoimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class PlayVideoActivity extends AppCompatActivity {
    VideoView videoPlaying;
    SeekBar seekBar;
    Toolbar toolbar;
    TextView tvTimePlay, tvTotalTime, tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
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
        videoPlaying.setVideoPath(b.getString("videoPath"));
        videoPlaying.start();

        tvTotalTime.setText(b.getString("videoDur") );
    }

    void AnhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        videoPlaying = findViewById(R.id.videoPlaying);
        seekBar = findViewById(R.id.seekbar);
        tvName= findViewById(R.id.tvName);
        tvTimePlay = findViewById(R.id.tvTimePlay);
        tvTotalTime = findViewById(R.id.tvTotalTime);
    }

}