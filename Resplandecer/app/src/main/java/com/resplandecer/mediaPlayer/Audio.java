package com.resplandecer.mediaPlayer;

public class Audio {

    private String author;
    private String title;
    private String audioUrl;

    public Audio(String title, String audioUrl, String author) {
        this.title = title;
        this.audioUrl = audioUrl;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getAuthor() {
        return author;
    }
}
