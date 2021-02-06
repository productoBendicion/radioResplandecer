package com.resplandecer.screens.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.resplandecer.R;
import com.resplandecer.utils.HomeData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.AudioViewHolder> {

    private ArrayList<HomeData> homeDataList;
    private Context context;
    private Listener listener;

    public HomeDataAdapter(Context context, Listener listener,  ArrayList<HomeData> homeDataList) {
        this.homeDataList = homeDataList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_data_holder, viewGroup, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder homeDataHolder, int position) {
        final HomeData homeData = homeDataList.get(position);
        String title = homeData.getTitle();

        if (title.equals("ESTACION PRINCIPAL")) {
            homeDataHolder.title.setText("Radio Resplandecer");
            homeDataHolder.contentData.setText("Precione Para Escuchar");
            homeDataHolder.itemView.setBackgroundColor(Color.RED);
            homeDataHolder.title.setTextColor(Color.WHITE);
            homeDataHolder.contentData.setTextColor(Color.WHITE);

            homeDataHolder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onViewClicked(homeData.getContext());
                        }
                    }
            );
        } else {
            homeDataHolder.title.setText(homeData.getTitle());
            homeDataHolder.contentData.setText(homeData.getContext());
        }



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

    interface Listener {

        void onViewClicked(String url);
    }
}
