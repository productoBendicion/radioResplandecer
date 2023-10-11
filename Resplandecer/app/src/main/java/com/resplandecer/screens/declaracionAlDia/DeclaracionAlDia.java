package com.resplandecer.screens.declaracionAlDia;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.resplandecer.R;
import com.resplandecer.baseActivities.BaseDrawerActivity;
import com.resplandecer.mediaPlayer.Audio;
import com.resplandecer.mediaPlayer.RadioPlayerListener;
import com.resplandecer.mediaPlayer.RadioStationUrls;
import com.resplandecer.mediaPlayer.SimplePlayer;
import com.resplandecer.mediaPlayer.StationType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeclaracionAlDia extends BaseDrawerActivity implements RadioPlayerListener, DeclaracionAlDiaAdapter.Listener {

    RecyclerView listOfDeclaraciones;
    DeclaracionAlDiaAdapter songsAdapter;
    RadioStationUrls radioStationUrls;
    SimplePlayer simplePlayer;
    private DeclaracionAlDiaRetriever declaracionAlDiaRetriever;
    private StationType stationType;
    private ArrayList<Audio> fetchedAudio;

    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_declaracion_al_dia, null, false);
        drawerLayout.addView(contentView, 0);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Declaracion Al Dia");
        }
        fetchedAudio = new ArrayList<>();

        declaracionAlDiaRetriever = new DeclaracionAlDiaRetriever();
        stationType = StationType.DECLARACION_AL_DIA;
        listOfDeclaraciones = findViewById(R.id.list_of_declaraciones);
        listOfDeclaraciones.setNestedScrollingEnabled(false);
        listOfDeclaraciones.setLayoutManager(new LinearLayoutManager(this));
        radioStationUrls = RadioStationUrls.initRadioStationUrl();

        simplePlayer = SimplePlayer.initializeSimplePlayer(this, this);

        songsAdapter = new DeclaracionAlDiaAdapter(this, this, new ArrayList<Audio>());
        listOfDeclaraciones.setAdapter(songsAdapter);

        declaracionAlDiaRetriever.execute();
    }

    public void showDeclaraciones(ArrayList<Audio> declaracions) {
        fetchedAudio.clear();
        fetchedAudio.addAll(declaracions);
        songsAdapter.updateDeclaraciones(declaracions);
    }

    @Override
    public void onRadioPlayerReady() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            startService();

            songsAdapter.updateCurrentSong(true, radioStationUrls.getCurrentSongTracker());
            songsAdapter.notifyDataSetChanged();
            hideDialog();
        }
    }

    @Override
    public void onRadioPlayerError() {
        stopService();

        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            hideDialog();
            showNetworkError("Error");

        }
    }

    @Override
    public void onRadioPlayerBuffering() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            showDialog();
        }
    }

    @Override
    public void onReleaseRadio() {
        stopService();

        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            hideDialog();
        }
    }

    @Override
    public void onRadioStateEnded() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            songsAdapter.updateCurrentSong(false, radioStationUrls.getCurrentSongTracker());
            songsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSongTapped(int selectedSong) {
        if (selectedSong == radioStationUrls.getCurrentSongTracker()) {
            simplePlayer.releasePlayer();
            songsAdapter.updateCurrentSong(false, selectedSong);
        } else {
            radioStationUrls.updateCurrentSongs(stationType, fetchedAudio);
            songsAdapter.notifyDataSetChanged();
            radioStationUrls.setCurrentTrack(selectedSong);
            simplePlayer.initPlayer();
        }
        songsAdapter.notifyDataSetChanged();


    }

    class DeclaracionAlDiaRetriever extends AsyncTask<String, Void, ArrayList<Audio>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Audio> doInBackground(String... params) {
            ArrayList<Audio> declaracions = new ArrayList<>();
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT3HsRGiTn6Lu7ie99Gh85WSpmT4aOXv9mNw2n49_5eFUbEnPPpbpaAtj7Qphj4wMd8WfaFofaTVv8H/pub?gid=0&single=true&output=csv");
                conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String inputLine;
                    int line = 0;
                    while ((inputLine = br.readLine()) != null) {
                        String[] comps = inputLine.split(",");


                        if (line != 0) {

                            String link = "";
                            String title = "";
                            String author = "";
                            for (int i = 0; i < comps.length; i++) {
                                if (i % 2 == 0) {
                                    link = comps[i];
                                } else if (i % 3 == 0) {
                                    author = comps[i];
                                } else {
                                    title = comps[i];
                                }
                            }


//                            Log.d("Declaraciones", "linK: " + link);
//                            Log.d("Declaraciones", "title: " + title);

                            if (link.contains("mp3")) {
                                Audio declaracion = new Audio(title, link, author);
                                declaracions.add(declaracion);
                            }
                        }
                        line++;
                    }
                }

            } catch (Exception e) {
                Log.e("Error", e.toString());
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return declaracions;
        }

        @Override
        protected void onPostExecute(ArrayList<Audio> result) {
            showDeclaraciones(result);
        }
    }
}
