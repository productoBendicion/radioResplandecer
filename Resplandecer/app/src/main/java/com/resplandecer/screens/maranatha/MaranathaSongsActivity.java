package com.resplandecer.screens.maranatha;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.resplandecer.R;
import com.resplandecer.baseActivities.BaseDrawerActivity;
import com.resplandecer.mediaPlayer.Audio;
import com.resplandecer.mediaPlayer.RadioPlayerListener;
import com.resplandecer.mediaPlayer.RadioStationMode;
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

public class MaranathaSongsActivity extends BaseDrawerActivity implements RadioPlayerListener, MaranathaSongsAdapter.Listener {

    private static final String TAG = "MaranathaSongsActivity";

    private RecyclerView listOfMaranathaSongs;
    private MaranathaSongsAdapter maranathaSongsAdapter;
    private RadioStationUrls radioStationUrls;
    private MaranathaSongsRetriever maranathaSongsRetriever;
    private SimplePlayer simplePlayer;
    private StationType stationType;
    private ArrayList<Audio> fetchedAudio;
    private FloatingActionButton shuffleButton;

    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_maranatha_songs, null, false);
        drawerLayout.addView(contentView, 0);
        maranathaSongsRetriever = new MaranathaSongsRetriever();
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Valverde Sr Himnos");
        }

        fetchedAudio = new ArrayList<>();

        stationType = StationType.HIMNOS_PASTOR_VALVERDE_SR;
        listOfMaranathaSongs = findViewById(R.id.list_of_maranatha_songs);
        shuffleButton = findViewById(R.id.shuffle_acion);
        listOfMaranathaSongs.setNestedScrollingEnabled(false);
        listOfMaranathaSongs.setLayoutManager(new LinearLayoutManager(this));
        radioStationUrls = RadioStationUrls.initRadioStationUrl();

        simplePlayer = SimplePlayer.initializeSimplePlayer(this, this);

        maranathaSongsAdapter = new MaranathaSongsAdapter(this, this, new ArrayList<Audio>());
        listOfMaranathaSongs.setAdapter(maranathaSongsAdapter);
        listOfMaranathaSongs.setHasFixedSize(true);

        maranathaSongsRetriever.execute();

        shuffleButton
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (radioStationUrls.getRadioStationMode() != RadioStationMode.RANDOM) {
                                    radioStationUrls.updateCurrentSongs(StationType.HIMNOS_PASTOR_VALVERDE_SR, fetchedAudio);
                                    radioStationUrls.setRadioStationMode(RadioStationMode.RANDOM);
                                    shuffleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                    simplePlayer.initPlayer();
                                } else {
                                    shuffleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selected_background)));

                                }
                            }
                        }
                );
    }


    @Override
    public void onRadioPlayerReady() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            startService();
            maranathaSongsAdapter.updateCurrentSong(true, radioStationUrls.getCurrentSongTracker());
            maranathaSongsAdapter.notifyDataSetChanged();
            hideDialog();
        }
    }

    @Override
    public void onRadioPlayerError() {
        stopService();
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            Log.d(TAG, "onRadioPlayerErrorListener");
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
            maranathaSongsAdapter.updateCurrentSong(false, radioStationUrls.getCurrentSongTracker());
            maranathaSongsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSongTapped(int selectedSong) {
        if (selectedSong == radioStationUrls.getCurrentSongTracker()) {
            simplePlayer.releasePlayer();
            maranathaSongsAdapter.updateCurrentSong(false, selectedSong);
        } else {
            radioStationUrls.updateCurrentSongs(StationType.HIMNOS_PASTOR_VALVERDE_SR, fetchedAudio);
            maranathaSongsAdapter.notifyDataSetChanged();
            radioStationUrls.setCurrentTrack(selectedSong);
            simplePlayer.initPlayer();
        }
        maranathaSongsAdapter.notifyDataSetChanged();
    }

    public void onMaranathaSongsFetched(ArrayList<Audio> maranathaSongs) {
        fetchedAudio.clear();
        fetchedAudio.addAll(maranathaSongs);
        maranathaSongsAdapter.updateSongs(maranathaSongs);
    }

    class MaranathaSongsRetriever extends AsyncTask<String, Void, ArrayList<Audio>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected ArrayList<Audio> doInBackground(String... params) {
            ArrayList<Audio> maranathaSongs = new ArrayList<>();
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT3HsRGiTn6Lu7ie99Gh85WSpmT4aOXv9mNw2n49_5eFUbEnPPpbpaAtj7Qphj4wMd8WfaFofaTVv8H/pub?gid=902385560&single=true&output=csv");
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
                                Audio maranathaSong = new Audio(title, link, author);
                                maranathaSongs.add(maranathaSong);
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
            return maranathaSongs;
        }

        @Override
        protected void onPostExecute(ArrayList<Audio> result) {
            onMaranathaSongsFetched(result);
            hideDialog();
        }
    }
}
