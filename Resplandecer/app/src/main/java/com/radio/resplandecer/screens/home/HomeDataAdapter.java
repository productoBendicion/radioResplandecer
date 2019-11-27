package com.radio.resplandecer.screens.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radio.resplandecer.R;
import com.radio.resplandecer.mediaPlayer.Audio;
import com.radio.resplandecer.utils.HomeData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.AudioViewHolder> {

    private ArrayList<HomeData> homeDataList;
    private Context context;

    public HomeDataAdapter(Context context, ArrayList<HomeData> homeDataList) {
        this.homeDataList = homeDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_data_holder, viewGroup, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder homeDataHolder, int position) {
        HomeData homeData = homeDataList.get(position);
        homeDataHolder.title.setText(homeData.getTitle());
        homeDataHolder.contentData.setText(homeData.getContext());

    }

    public void updateVdeeAudios(ArrayList<HomeData> audioList) {
        this.homeDataList.clear();
        this.homeDataList.addAll(audioList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return homeDataList.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView contentData;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            contentData = itemView.findViewById(R.id.content_data);

        }
    }
}
