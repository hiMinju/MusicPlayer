package com.example.hw3;

import android.net.Uri;

public class Music {
    private Uri imageUri;
    private long id;
    private long albumId;
    private String title = "";
    private String artist = "";
    private String dataPath = "";
    private String album = "";
    private long duration;

    public Music(long id, long albumId, String title, String artist, String album, String dataPath, long duration) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.dataPath = dataPath;
        this.album = album;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
