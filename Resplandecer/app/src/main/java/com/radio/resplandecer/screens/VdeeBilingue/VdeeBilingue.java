package com.radio.resplandecer.screens.VdeeBilingue;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.radio.resplandecer.R;
import com.radio.resplandecer.baseActivities.BaseDrawerActivity;
import com.radio.resplandecer.mediaPlayer.Audio;
import com.radio.resplandecer.mediaPlayer.RadioPlayerListener;
import com.radio.resplandecer.mediaPlayer.RadioStationUrls;
import com.radio.resplandecer.mediaPlayer.SimplePlayer;
import com.radio.resplandecer.mediaPlayer.StationType;
import com.radio.resplandecer.screens.declaracionAlDia.DeclaracionAlDiaAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VdeeBilingue extends BaseDrawerActivity implements DeclaracionAlDiaAdapter.Listener, VdeeBilingueAdapter.Listener, RadioPlayerListener {

    RecyclerView listOfAudios;
    VdeeBilingueAdapter vdeeBilingueAdapter;
    RadioStationUrls radioStationUrls;
    SimplePlayer simplePlayer;
    private VDEEBilingueFetcher vdeeBilingueFetcher;
    private FloatingActionButton playStopFloatingButton;
    private StationType stationType;
    private ArrayList<Audio> fetchedAudio;

    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_vdee_bilingue, null, false);
        drawerLayout.addView(contentView, 0);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("VDEE Bilingue");
        }

        fetchedAudio = new ArrayList<>();

        vdeeBilingueFetcher = new VDEEBilingueFetcher();
        stationType = StationType.VDEE_BILINGUE;

        listOfAudios = findViewById(R.id.list_of_vdee_bilingue);

        listOfAudios.setNestedScrollingEnabled(false);
        listOfAudios.setLayoutManager(new LinearLayoutManager(this));
        radioStationUrls = RadioStationUrls.initRadioStationUrl();
        playStopFloatingButton = findViewById(R.id.play_all_action);


        simplePlayer = SimplePlayer.initializeSimplePlayer(this, this);

        vdeeBilingueAdapter = new VdeeBilingueAdapter(this, this, new ArrayList<Audio>());
        listOfAudios.setAdapter(vdeeBilingueAdapter);

        vdeeBilingueFetcher.execute();

        playStopFloatingButton
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (simplePlayer.isInitialized()
                                        && stationType == radioStationUrls.getCurrentRadioStationType()) {
                                    simplePlayer.releasePlayer();
                                } else {
                                    if (stationType == radioStationUrls.getCurrentRadioStationType()) {
                                        radioStationUrls.updateCurrentSongs(stationType, fetchedAudio);
                                        simplePlayer.initPlayer();
                                    }
                                }
                            }
                        }
                );
    }

    public void onVdeeBilingueFetched(ArrayList<Audio> vdeeBilingueAudios) {
        fetchedAudio.clear();
        fetchedAudio.addAll(vdeeBilingueAudios);
        vdeeBilingueAdapter.updateVdeeAudios(vdeeBilingueAudios);
    }

    @Override
    public void onSongTapped(int selectedSong) {
        if (stationType != radioStationUrls.getCurrentRadioStationType()) {
            radioStationUrls.updateCurrentSongs(stationType, fetchedAudio);
        }
        vdeeBilingueAdapter.notifyDataSetChanged();
        radioStationUrls.setCurrentTrack(selectedSong);
        simplePlayer.initPlayer();
    }

    @Override
    public void onRadioPlayerReady() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            playStopFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.stop_icon));
            playStopFloatingButton.setVisibility(View.VISIBLE);
            hideDialog();
        }
    }

    @Override
    public void onRadioPlayerError() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            playStopFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
            playStopFloatingButton.setVisibility(View.VISIBLE);

            hideDialog();
            showNetworkError("Error");

        }
    }

    @Override
    public void onRadioPlayerBuffering() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            playStopFloatingButton.setVisibility(View.GONE);
            showDialog();
        }
    }

    @Override
    public void onReleaseRadio() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            playStopFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
            playStopFloatingButton.setVisibility(View.VISIBLE);
            hideDialog();
        }
    }

    @Override
    public void onRadioStateEnded() {
        if (stationType == radioStationUrls.getCurrentRadioStationType()) {
            vdeeBilingueAdapter.notifyDataSetChanged();
        }
    }

    class VDEEBilingueFetcher extends AsyncTask<String, Void, ArrayList<Audio>>  {

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
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT3HsRGiTn6Lu7ie99Gh85WSpmT4aOXv9mNw2n49_5eFUbEnPPpbpaAtj7Qphj4wMd8WfaFofaTVv8H/pub?gid=1477281102&single=true&output=csv");
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

                            Log.d("Declaraciones", "linK: " + link);
                            Log.d("Declaraciones", "title: " + title);

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
            onVdeeBilingueFetched(result);
            hideDialog();
        }
    }
}
