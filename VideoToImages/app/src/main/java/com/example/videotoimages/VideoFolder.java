package com.example.videotoimages;

public class VideoFolder {
    String folderName;
    int folderSize;

    public VideoFolder() {
    }

    public VideoFolder(String folderName, int folderSize) {
        this.folderName = folderName;
        this.folderSize = folderSize;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderSize() {
        return folderSize;
    }

    public void setFolderSize(int folderSize) {
        this.folderSize = folderSize;
    }
}
