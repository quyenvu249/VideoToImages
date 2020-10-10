package com.example.videotoimages.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.videotoimages.fragment.PhotosGalleryFragment;
import com.example.videotoimages.R;
import com.example.videotoimages.fragment.VideoGalleryFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public final int[] TAB_FILES = new int[]{R.string.images, R.string.videos};
    Context context;

    public ViewPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PhotosGalleryFragment playlistFragment = PhotosGalleryFragment.newInstance();
                return playlistFragment;

            case 1:
                VideoGalleryFragment fragment1 = VideoGalleryFragment.newInstance();
                return fragment1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_FILES[position]);
    }
}
