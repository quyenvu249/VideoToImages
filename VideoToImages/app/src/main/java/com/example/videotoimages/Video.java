package com.example.videotoimages;

import android.net.Uri;

public class Video {
    String name;
    String path;
    String thumb;
    String duration;
    int size;

    public Video( String name,String path, String thumb, String duration, int size) {
        //this.uriVid = uriVid;
        this.name = name;
        this.path = path;
        this.thumb = thumb;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
