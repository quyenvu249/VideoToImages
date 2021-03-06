package com.example.videotoimages.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Video {
    String name;
    String path;
    Bitmap bitmap;
    int duration;
    int size;

    public Video( String name,String path, Bitmap bitmap, int duration, int size) {
        //this.uriVid = uriVid;
        this.name = name;
        this.path = path;
        this.bitmap = bitmap;
        this.duration = duration;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Video() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //    public String getThumb() {
//        return thumb;
//    }
//
//    public void setThumb(String thumb) {
//        this.thumb = thumb;
//    }

//    public Uri getUriVid() {
//        return uriVid;
//    }
//
//    public void setUriVid(Uri uriVid) {
//        this.uriVid = uriVid;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
