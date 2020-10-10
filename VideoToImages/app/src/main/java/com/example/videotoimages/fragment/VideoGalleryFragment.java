package com.example.videotoimages.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.videotoimages.R;

public class VideoGalleryFragment extends Fragment {


    public VideoGalleryFragment() {
    }


    public static VideoGalleryFragment newInstance() {
        VideoGalleryFragment fragment = new VideoGalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_gallery, container, false);
    }
}