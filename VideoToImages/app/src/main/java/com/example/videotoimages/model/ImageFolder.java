package com.example.videotoimages.model;

public class ImageFolder {
    String imageFolderName;
    int imageFolderSize;

    public ImageFolder() {
    }

    public ImageFolder(String imageFolderName, int imageFolderSize) {
        this.imageFolderName = imageFolderName;
        this.imageFolderSize = imageFolderSize;
    }

    public String getImageFolderName() {
        return imageFolderName;
    }

    public void setImageFolderName(String imageFolderName) {
        this.imageFolderName = imageFolderName;
    }

    public int getImageFolderSize() {
        return imageFolderSize;
    }

    public void setImageFolderSize(int imageFolderSize) {
        this.imageFolderSize = imageFolderSize;
    }
}
