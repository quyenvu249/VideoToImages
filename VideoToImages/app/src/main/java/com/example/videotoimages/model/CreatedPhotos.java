package com.example.videotoimages.model;

import android.graphics.Bitmap;

public class CreatedPhotos {
    Bitmap bitmap;
    String photoName;
    String photoPath;
    int photoSize;

    public CreatedPhotos() {
    }

    public CreatedPhotos(Bitmap bitmap, String photoName, String photoPath, int photoSize) {
        this.bitmap = bitmap;
        this.photoName = photoName;
        this.photoPath = photoPath;
        this.photoSize = photoSize;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

