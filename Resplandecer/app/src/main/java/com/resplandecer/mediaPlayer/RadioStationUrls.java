package com.resplandecer.mediaPlayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class RadioStationUrls {

    private int currentSong;
    private static RadioStationUrls radioStationUrls;
    private ArrayList<Audio> currentSongs;
    private RadioStationMode radioStationMode;
    private StationType currentRadioStationType;
    private Random random;

    private RadioStationUrls() {
        currentSong = 0;
        currentSongs = new ArrayList<>();
        radioStationMode = RadioStationMode.DEFAULT;
        random = new Random();
    }

    public static RadioStationUrls initRadioStationUrl() {
        if (radioStationUrls == null) {
            radioStationUrls = new RadioStationUrls();
        }

        return radioStationUrls;
    }

    public void updateCurrentSongs(StationType currentRadioStationType, ArrayList<Audio> currentSongs) {
        this.currentSongs.clear();
        this.currentRadioStationType = currentRadioStationType;
        this.currentSongs.addAll(currentSongs);
        currentSong = 0;
    }

    public StationType getCurrentRadioStationType() {
        return currentRadioStationType;
    }

    public void setCurrentTrack(int currentSong) {
        this.currentSong = currentSong;
    }

    public void setRadioStationMode(RadioStationMode radioStationMode) {
        this.radioStationMode = radioStationMode;
        if (radioStationMode == RadioStationMode.RANDOM) {
            nextSong();
        }
    }

    public RadioStationMode getRadioStationMode() {
        return radioStationMode;
    }


    public Audio getCurrentSong() {
        return currentSongs.get(currentSong);
    }

    public int getCurrentSongTracker() {
        return currentSong;
    }

    public void nextSong() {

        if (radioStationMode == RadioStationMode.RANDOM) {
            currentSong = getRandomSong();

        } else {
            if (currentSong == currentSongs.size() - 1) {
                currentSong = 0;
            } else {
                currentSong++;
            }
        }

//        Log.d("CurrentSong: " , "Curr: " +  currentSong);
    }

    private int getRandomSong() {
        Random rand = new Random();
        return rand.nextInt(currentSongs.size());
    }
}
