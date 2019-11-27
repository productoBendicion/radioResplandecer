package com.radio.resplandecer.screens.home;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.radio.resplandecer.R;
import com.radio.resplandecer.baseActivities.BaseDrawerActivity;
import com.radio.resplandecer.mediaPlayer.Audio;
import com.radio.resplandecer.mediaPlayer.RadioPlayerListener;
import com.radio.resplandecer.mediaPlayer.RadioStationUrls;
import com.radio.resplandecer.mediaPlayer.SimplePlayer;
import com.radio.resplandecer.mediaPlayer.StationType;
import com.radio.resplandecer.utils.HomeData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends BaseDrawerActivity implements RadioPlayerListener {

    RecyclerView listOfHomeData;
    HomeDataAdapter homeDataAdapter;
    private HomeDataFetcher homeDataFetcher;
    private ArrayList<HomeData> homeDataArrayList;
    private ImageButton playStopHome;
    private SimplePlayer simplePlayer;
    private RadioStationUrls radioStationUrls;
    private ArrayList<Audio> homeAudio;


    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawerLayout.addView(contentView, 0);

        homeDataFetcher = new HomeDataFetcher();
        homeDataArrayList = new ArrayList<>();

        listOfHomeData = findViewById(R.id.list_of_home_data);
        playStopHome = findViewById(R.id.play_stop_home);
        listOfHomeData.setLayoutManager(new LinearLayoutManager(this));
        listOfHomeData.setNestedScrollingEnabled(false);
        radioStationUrls = RadioStationUrls.initRadioStationUrl();

        simplePlayer = SimplePlayer.initializeSimplePlayer(this, this);


        homeDataAdapter = new HomeDataAdapter(this, new ArrayList<HomeData>());
        homeAudio = new ArrayList<>();
        homeAudio.add(new Audio("Radio Resplandecer", "http://107.215.165.202:8000/resplandecer?hash=1573611071190.mp3", ""));
        listOfHomeData.setAdapter(homeDataAdapter);

        playStopHome
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (simplePlayer.isInitialized() && StationType.HOME == radioStationUrls.getCurrentRadioStationType()) {
                                    simplePlayer.releasePlayer();
                                    stopService();
                                } else {
                                    radioStationUrls.updateCurrentSongs(StationType.HOME, homeAudio);
                                    simplePlayer.initPlayer();
                                    startService();
                                }
                            }
                        }
                );

        homeDataFetcher.execute();

    }

    public void onHomeDataFetched(ArrayList<HomeData> homeDataArrayList) {
        this.homeDataArrayList.clear();
        this.homeDataArrayList.addAll(homeDataArrayList);
        homeDataAdapter.updateVdeeAudios(homeDataArrayList);

    }

    @Override
    public void onRadioPlayerReady() {
        if (StationType.HOME == radioStationUrls.getCurrentRadioStationType()) {
            playStopHome.setImageDrawable(getResources().getDrawable(R.drawable.home_stop_icon));
            playStopHome.setVisibility(View.VISIBLE);
            hideDialog();
        }
    }

    @Override
    public void onRadioPlayerError() {
        if (StationType.HOME == radioStationUrls.getCurrentRadioStationType()) {
            playStopHome.setImageDrawable(getResources().getDrawable(R.drawable.home_play_icon));

            hideDialog();
            showNetworkError("Network Error");
        }
    }

    @Override
    public void onRadioPlayerBuffering() {
        if (StationType.HOME == radioStationUrls.getCurrentRadioStationType()) {
            showDialog();
        }
    }

    @Override
    public void onReleaseRadio() {
        if (StationType.HOME == radioStationUrls.getCurrentRadioStationType()) {
            playStopHome.setImageDrawable(getResources().getDrawable(R.drawable.home_play_icon));
            hideDialog();
        }
    }

    @Override
    public void onRadioStateEnded() {

    }

class HomeDataFetcher extends AsyncTask<String, Void, ArrayList<HomeData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected ArrayList<HomeData> doInBackground(String... params) {
            ArrayList<HomeData> homeDataList = new ArrayList<>();
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT3HsRGiTn6Lu7ie99Gh85WSpmT4aOXv9mNw2n49_5eFUbEnPPpbpaAtj7Qphj4wMd8WfaFofaTVv8H/pub?gid=939886657&single=true&output=csv");
                conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String inputLine;
                    int line = 0;
                    while ((inputLine = br.readLine()) != null) {
                        String[] comps = inputLine.split(",");


                        if (line != 0) {

                            String title = "";
                            String context = "";
                            for (int i = 0; i < comps.length; i++) {
                                Log.d("HomeData", "i: " + i + " data: " + comps[i]);
                                if (i % 2 == 0) {
                                    context = comps[i];
                                } else {
                                    if (i != 0) {
                                        title = comps[i];
                                    }
                                }
                            }

                            Log.d("HomeData", "title: " + title);
                            Log.d("HomeData", "content: " + context);

                            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(context)) {
                                HomeData homeData = new HomeData(title, context);
                                homeDataList.add(homeData);
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
            return homeDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<HomeData> result) {
            hideDialog();
            onHomeDataFetched(result);
        }
    }
}
