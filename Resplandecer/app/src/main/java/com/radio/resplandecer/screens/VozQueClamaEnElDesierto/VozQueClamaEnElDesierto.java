package com.radio.resplandecer.screens.VozQueClamaEnElDesierto;

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
import com.radio.resplandecer.screens.declaracionAlDia.DeclaracionAlDia;
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

public class VozQueClamaEnElDesierto extends BaseDrawerActivity implements DeclaracionAlDiaAdapter.Listener, RadioPlayerListener, VozQueClamaEnElDesiertoAdapter.Listener {

    RecyclerView listOfVozQueClamaEnElDesierto;
    VozQueClamaEnElDesiertoAdapter songsAdapter;
    RadioStationUrls radioStationUrls;
    SimplePlayer simplePlayer;
    private VozQueClamaEnElDesiertoRetriever vozQueClamaEnElDesiertoRetriever;

    private FloatingActionButton playStopFloatingButton;
    private StationType stationType;
    private ArrayList<Audio> fetchedAudio;

    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_voz_que_clama_en_el_desierto, null, false);
        drawerLayout.addView(contentView, 0);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Voz Que Clama En El Desierto");
        }

        fetchedAudio = new ArrayList<>();
        stationType = StationType.VOZ_QUE_CLAMA_EN_EL_DESIERTO;


        vozQueClamaEnElDesiertoRetriever = new VozQueClamaEnElDesiertoRetriever();
        playStopFloatingButton = findViewById(R.id.play_all_action);

        listOfVozQueClamaEnElDesierto = findViewById(R.id.list_of_voz_que_clama_en_el_desierto);
        listOfVozQueClamaEnElDesierto.setNestedScrollingEnabled(false);
        listOfVozQueClamaEnElDesierto.setLayoutManager(new LinearLayoutManager(this));
        radioStationUrls = RadioStationUrls.initRadioStationUrl();

        simplePlayer = SimplePlayer.initializeSimplePlayer(this, this);

        songsAdapter = new VozQueClamaEnElDesiertoAdapter(this, this, new ArrayList<Audio>());
        listOfVozQueClamaEnElDesierto.setAdapter(songsAdapter);

        vozQueClamaEnElDesiertoRetriever.execute();

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

    public void updateVozDelEvangelioEterno(ArrayList<Audio> declaracions) {
        fetchedAudio.clear();
        fetchedAudio.addAll(declaracions);
        songsAdapter.updateVozDelEvangelioEterno(declaracions);
    }

    @Override
    public void onSongTapped(int selectedSong) {
        if (stationType != radioStationUrls.getCurrentRadioStationType()) {
            radioStationUrls.updateCurrentSongs(stationType, fetchedAudio);
        }
        songsAdapter.notifyDataSetChanged();
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
            songsAdapter.notifyDataSetChanged();
        }
    }


    class VozQueClamaEnElDesiertoRetriever extends AsyncTask<String, Void, ArrayList<Audio>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Audio> doInBackground(String... params) {
            ArrayList<Audio> declaracions = new ArrayList<>();
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT3HsRGiTn6Lu7ie99Gh85WSpmT4aOXv9mNw2n49_5eFUbEnPPpbpaAtj7Qphj4wMd8WfaFofaTVv8H/pub?gid=1203218903&single=true&output=csv");
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
            updateVozDelEvangelioEterno(result);
        }
    }

}
